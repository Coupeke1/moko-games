import Column from "@/components/layout/column";
import FriendCard from "@/features/chat/components/channel-card/friend-card";
import { useFriends } from "@/features/friends/hooks/use-friends";
import type { Friend } from "@/features/friends/models/friend";

export default function Friends() {
    const { friends, loading, error } = useFriends();
    if (!friends || loading || error) return null;

    return (
        <Column>
            {friends.map((friend: Friend) => (
                <FriendCard key={friend.id} friend={friend} selected={false} />
            ))}
        </Column>
    );
}
