import Input from "@/components/inputs/input";
import Select from "@/components/inputs/select";
import Column from "@/components/layout/column";
import Message from "@/components/state/message";
import type { Game } from "@/models/library/game";
import LobbySettings from "@/routes/library/components/lobby-settings";
import { useState } from "react";

function TicTacToeSettings() {
    const [size, setSize] = useState(3);

    return (
        <Column>
            <Input label="Board Size" type="number" value={size.toString()} onChange={(e) => setSize(Number(e.target.value))} />
        </Column>
    )
}

function CheckersSettings() {
    const [size, setSize] = useState(3);
    const [flyingKings, setFlyingKings] = useState("disabled");

    return (
        <Column>
            <Input label="Board Size" type="number" value={size.toString()} onChange={(e) => setSize(Number(e.target.value))} />
            <Select label="Flying Kings" value={flyingKings} onChange={(e) => setFlyingKings(e.target.value)} options={[
                { label: "Disabled", value: "disabled" },
                { label: "Enabled", value: "enabled" }
            ]} />
        </Column>
    )
}

interface Props {
    game: Game;
}

export default function GameSettings({ game }: Props) {
    if (game.title === "Tic Tac Toe") return <TicTacToeSettings />
    else if (game.title === "Checkers") return <CheckersSettings />
    else return <Message>Settings not found :(</Message>
}