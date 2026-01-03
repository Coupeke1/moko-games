import ChannelCard from "@/features/chat/components/channel-card/channel-card";
import type { Friend } from "@/features/friends/models/friend";

interface Props {
    friend: Friend;
    selected: boolean;
    onSelect: (id: string) => void;
}

export default function FriendCard({ friend, selected, onSelect }: Props) {
    return (
        <ChannelCard
            username={friend.username}
            image={friend.image}
            description={`Level ${friend.statistics.level}`}
            selected={selected}
            onSelect={() => onSelect(friend.id)}
        />
    );
}
