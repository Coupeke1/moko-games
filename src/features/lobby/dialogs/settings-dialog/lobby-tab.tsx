import Input from "@/components/inputs/input";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import type { Lobby } from "@/features/lobby/models/lobby.ts";

interface Props {
    lobby: Lobby;
    isOwner: boolean;
    size: string;
    setSize: (size: number) => void;
}

export default function LobbyTab({ lobby, isOwner, size, setSize }: Props) {
    function validate(value: string) {
        if (isNaN(Number(value))) return;
        setSize(Number(value));
    }

    return (
        <Column gap={Gap.Large}>
            <Input
                label="Max Players"
                disabled={!isOwner}
                type="number"
                min={lobby.players.length}
                max={8}
                value={size}
                onChange={(e) => validate(e.target.value)}
            />
        </Column>
    );
}
