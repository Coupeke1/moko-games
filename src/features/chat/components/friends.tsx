import Column from "@/components/layout/column";
import ChannelCard from "@/features/chat/components/channel-card";
import { useFriends } from "@/features/friends/hooks/use-friends";
import type { Friend } from "@/features/friends/models/friend";

interface Props {
    selected: string | null;
    onSelect: (id: string) => void;
}

export default function FriendsSection({ selected, onSelect }: Props) {
    const { friends, loading, error } = useFriends();
    if (friends.length <= 0 || loading || error) return null;

    return (
        <Column>
            {friends.map((friend: Friend) => (
                <ChannelCard
                    key={friend.id}
                    username={friend.username}
                    image={friend.image}
                    description={`Level ${friend.statistics.level}`}
                    selected={selected === friend.id}
                    onSelect={() => onSelect(friend.id)}
                />
            ))}
        </Column>
    );
}
