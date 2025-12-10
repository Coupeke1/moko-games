import {Row} from "@/components/layout/row.tsx";
import {type GameState} from "../models/game-state";

interface GameStateDisplayProps {
    gameState: GameState;
}

export function GameStateDisplay({gameState}: GameStateDisplayProps) {
    return (
        <section className="game-info bg-bg-2 p-4 rounded-lg flex flex-col gap-2">
            <Row label="Game Status" value={gameState.status}/>
            <Row label="Players" value={gameState.players.length}/>
        </section>
    );
}