import Input from "@/components/inputs/input";

interface Props {
    size: number;
    setSize: (players: number) => void;
}

export default function LobbySettings({ size, setSize }: Props) {
    return (
        <Input label="Max Players" type="number" value={size.toString()} onChange={(e) => setSize(Number(e.target.value))} />
    )
}