import bB from "./pieces/bB.svg";
import bK from "./pieces/bK.svg";
import bN from "./pieces/bN.svg";
import bP from "./pieces/bP.svg";
import bQ from "./pieces/bQ.svg";
import bR from "./pieces/bR.svg";
import wB from "./pieces/wB.svg";
import wK from "./pieces/wK.svg";
import wN from "./pieces/wN.svg";
import wP from "./pieces/wP.svg";
import wQ from "./pieces/wQ.svg";
import wR from "./pieces/wR.svg";

// Figury Cburnetta (Colin M.L. Burnett), ten sam zestaw co na Lichess.
// Licencja grafik: GPL-2.0-or-later albo CC-BY-SA-3.0.
const ART = {
  WHITE: {
    KING: wK,
    QUEEN: wQ,
    ROOK: wR,
    BISHOP: wB,
    KNIGHT: wN,
    PAWN: wP,
  },
  BLACK: {
    KING: bK,
    QUEEN: bQ,
    ROOK: bR,
    BISHOP: bB,
    KNIGHT: bN,
    PAWN: bP,
  },
};

export default function Piece({ type, color }) {
  const src = ART[color]?.[type];
  if (!src) {
    const tone = color === "WHITE" ? "white" : "black";
    return (
      <span className={`piece fallback ${tone}`} aria-hidden="true">
        {type.slice(0, 1)}
      </span>
    );
  }
  return <img className="piece" src={src} alt="" draggable={false} aria-hidden="true" />;
}
