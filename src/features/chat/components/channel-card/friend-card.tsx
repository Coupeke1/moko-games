import ChannelCard from "@/features/chat/components/channel-card/channel-card";
import type { Friend } from "@/features/friends/models/friend";

interface Props {
    friend: Friend;
    selected: boolean;
}

export default function FriendCard({ friend, selected }: Props) {
    return (
        <ChannelCard
            username={friend.username}
            image={friend.image}
            timestamp="15m ago"
            selected={selected}
        />
    );
}
