import { useEffect, useRef, useState } from "react";
import Piece from "./pieces";

const COLUMNS = ["A", "B", "C", "D", "E", "F", "G", "H"];
const ROWS = ["R8", "R7", "R6", "R5", "R4", "R3", "R2", "R1"];
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

  function onSquare(column, row) {
    if (game.mate) {
      return;
    }
    send("/api/game/click", {
      method: "POST",
      body: JSON.stringify({ column, row }),
    });
  }

  function onNewGame() {
    send("/api/game", { method: "POST" });
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

  const legal = new Map(game.legalMoves.map((move) => [`${move.column}:${move.row}`, move.capture]));
  const side = game.turn === "WHITE" ? "białe" : "czarne";
  const winner = game.turn === "WHITE" ? "czarne" : "białe";

  return (
    <main className="page">
      <section className="panel">
        <header>
          <div>
            <h1>Szachy</h1>
            {game.mate ? (
              <p className="turn mate">Mat. Wygrały {winner}.</p>
            ) : (
              <p className="turn">
                <span className={`swatch ${game.turn === "WHITE" ? "white" : "black"}`} />
                Teraz grają {side}
              </p>
            )}
          </div>
          <button type="button" className="new-game" onClick={onNewGame}>
            Nowa partia
          </button>
        </header>

        <div className="frame">
          <div className="board" aria-label="Szachownica">
            {ROWS.map((row) => (
              <div className="rank" key={row}>
                {COLUMNS.map((column, index) => {
                  const number = rowNumber(row);
                  const name = `${column}${number}`;
                  const piece = game.board[name];
                  const selected = game.selected?.column === column && game.selected?.row === row;
                  const capture = legal.get(`${column}:${row}`);
                  const light = (index + number) % 2 === 0;
                  const classes = [
                    "square",
                    light ? "light" : "dark",
                    selected ? "selected" : "",
                  ]
                    .filter(Boolean)
                    .join(" ");

                  return (
                    <button
                      key={name}
                      type="button"
                      className={classes}
                      aria-pressed={selected}
                      aria-label={squareLabel(column, number, piece)}
                      onClick={() => onSquare(column, row)}
                    >
                      {piece && <Piece type={piece.type} color={piece.color} />}
                      {capture != null && <span className={capture ? "mark capture" : "mark quiet"} />}
                      {column === "A" && <span className="coord rank-label">{number}</span>}
                      {number === 1 && <span className="coord file-label">{column}</span>}
                    </button>
                  );
                })}
              </div>
            ))}
          </div>
        </div>

        <p className="hint">
          Kliknij figurę gracza przy ruchu. Kropka oznacza wolne pole, obręcz bicie. Inne pole
          cofa zaznaczenie. Pion na końcu planszy staje się hetmanem. Przy macie partia się kończy.
        </p>
        {error && <p className="error">{error}</p>}
      </section>
    </main>
  );
}

function rowNumber(row) {
  return Number(row.slice(1));
}

function squareLabel(column, rank, piece) {
  const square = `${column}${rank}`;
  if (!piece) {
    return square;
  }
  const side = piece.color === "WHITE" ? "białych" : "czarnych";
  const name = PIECE_NAMES[piece.type] ?? piece.type.toLowerCase();
  return `${square}, ${name} ${side}`;
}
