import ChannelCard from "@/features/chat/components/channel-card/channel-card";

interface Props {
    selected: boolean;
}

export default function BotCard({ selected }: Props) {
    return (
        <ChannelCard
            username="Moko"
            image="https://cdn2.thecatapi.com/images/MTUwNjE4MQ.jpg"
            timestamp="15m ago"
            selected={selected}
        />
    );
}
