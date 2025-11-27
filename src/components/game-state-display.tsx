import type {GameState} from "@/models/game-state";
import Row from "@/components/layout/row.tsx";

interface Props {
    gameState: GameState;
}

export default function GameStateDisplay({gameState}: Props) {
    return (
        <section className="game-info bg-bg-2 p-4 rounded-lg flex flex-col gap-2">
            <Row label="Game Status" value={gameState.status}/>
            <Row label="Players" value={gameState.players.length}/>
        </section>
    );
}