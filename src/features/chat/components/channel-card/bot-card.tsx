import ChannelCard from "@/features/chat/components/channel-card/channel-card";

interface Props {
    selected: boolean;
    onSelect: () => void;
}

export default function BotCard({ selected, onSelect }: Props) {
    return (
        <ChannelCard
            username="Moko"
            image="https://cdn2.thecatapi.com/images/MTUwNjE4MQ.jpg"
            description="Beep Boop"
            selected={selected}
            onSelect={() => onSelect()}
        />
    );
}
