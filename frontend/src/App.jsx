import { useEffect, useRef, useState } from "react";
import Piece from "./pieces";

const FILES = ["a", "b", "c", "d", "e", "f", "g", "h"];
const PIECE_NAMES = {
  KING: "król",
  QUEEN: "hetman",
  ROOK: "wieża",
  BISHOP: "goniec",
  KNIGHT: "skoczek",
  PAWN: "pion",
};

export default function App() {
  const [game, setGame] = useState(null);
  const [error, setError] = useState("");
  const pending = useRef(false);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      for (let attempt = 0; attempt < 40 && !cancelled; attempt += 1) {
        try {
          const response = await fetch("/api/game");
          if (response.ok) {
            const body = await response.json();
            if (!cancelled) {
              setGame(body);
              setError("");
            }
            return;
          }
        } catch {
          // Backend może jeszcze wstawać, szczególnie w Dockerze.
        }
        await new Promise((resolve) => setTimeout(resolve, 400));
      }
      if (!cancelled) {
        setError("Nie udało się wczytać partii. Uruchom backend i odśwież stronę.");
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  async function send(url, options) {
    if (pending.current) {
      return;
    }
    pending.current = true;
    try {
      const response = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...options,
      });
      if (!response.ok) {
        throw new Error(String(response.status));
      }
      setGame(await response.json());
      setError("");
    } catch {
      setError("Brak połączenia z serwerem.");
    } finally {
      pending.current = false;
    }
  }

  function onSquare(row, col) {
    send("/api/game/click", {
      method: "POST",
      body: JSON.stringify({ row, col }),
    });
  }

  function onReset() {
    send("/api/game/reset", { method: "POST" });
  }

  if (!game) {
    return (
      <main className="page">
        <section className="panel">
          <h1>Szachy</h1>
          <p className="hint">{error || "Ładowanie partii…"}</p>
        </section>
      </main>
    );
  }

  const legal = new Map(game.legalMoves.map((move) => [`${move.row}:${move.col}`, move.capture]));
  const side = game.turn === "WHITE" ? "białe" : "czarne";

  return (
    <main className="page">
      <section className="panel">
        <header>
          <div>
            <h1>Szachy</h1>
            <p className="turn">
              <span className={`swatch ${game.turn === "WHITE" ? "white" : "black"}`} />
              Teraz grają {side}
            </p>
          </div>
          <button type="button" className="reset" onClick={onReset}>
            Nowa partia
          </button>
        </header>

        <div className="frame">
          <div className="board" aria-label="Szachownica">
            {game.board.map((rank, row) => (
              <div className="rank" key={row}>
                {rank.map((piece, col) => {
                  const key = `${row}:${col}`;
                  const selected =
                    game.selected != null && game.selected.row === row && game.selected.col === col;
                  const capture = legal.get(key);
                  const light = (row + col) % 2 === 0;
                  const classes = [
                    "square",
                    light ? "light" : "dark",
                    selected ? "selected" : "",
                  ]
                    .filter(Boolean)
                    .join(" ");

                  return (
                    <button
                      key={key}
                      type="button"
                      className={classes}
                      aria-pressed={selected}
                      aria-label={squareLabel(row, col, piece)}
                      onClick={() => onSquare(row, col)}
                    >
                      {piece && <Piece type={piece.type} color={piece.color} />}
                      {capture != null && <span className={capture ? "mark capture" : "mark quiet"} />}
                      {col === 0 && <span className="coord rank-label">{8 - row}</span>}
                      {row === 7 && <span className="coord file-label">{FILES[col]}</span>}
                    </button>
                  );
                })}
              </div>
            ))}
          </div>
        </div>

        <p className="hint">
          Kliknij figurę gracza przy ruchu. Kropka oznacza wolne pole, obręcz bicie. Inne pole
          cofa zaznaczenie. Pion na końcu planszy staje się hetmanem. Gra nie zatrzymuje się na
          macie ani remisie.
        </p>
        {error && <p className="error">{error}</p>}
      </section>
    </main>
  );
}

function squareLabel(row, col, piece) {
  const square = `${FILES[col]}${8 - row}`;
  if (!piece) {
    return square;
  }
  const side = piece.color === "WHITE" ? "białych" : "czarnych";
  return `${square}, ${PIECE_NAMES[piece.type]} ${side}`;
}
