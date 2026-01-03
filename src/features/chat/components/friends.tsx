import Column from "@/components/layout/column";
import FriendCard from "@/features/chat/components/channel-card/friend-card";
import { useFriends } from "@/features/friends/hooks/use-friends";
import type { Friend } from "@/features/friends/models/friend";

interface Props {
    selected: string;
    onSelect: (id: string) => void;
}

export default function FriendsSection({ selected, onSelect }: Props) {
    const { friends, loading, error } = useFriends();
    if (friends.length <= 0 || loading || error) return null;

    return (
        <Column>
            {friends.map((friend: Friend) => (
                <FriendCard
                    key={friend.id}
                    friend={friend}
                    selected={selected === friend.id}
                    onSelect={(id: string) => onSelect(id)}
                />
            ))}
        </Column>
    );
}
