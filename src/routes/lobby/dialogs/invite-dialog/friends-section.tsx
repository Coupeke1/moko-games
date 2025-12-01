import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import AcceptIcon from "@/components/icons/accept-icon";
import PlusIcon from "@/components/icons/plus-icon";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import showToast from "@/components/toast";
import { useFriends } from "@/hooks/use-friends";
import type { Lobby } from "@/models/lobby/lobby";
import type { Profile } from "@/models/profile/profile";
import { isPlayerInLobby, sendInvite } from "@/services/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface Props {
    lobby: Lobby;
    onInvite: () => void;
}

export default function FriendsSection({ lobby, onInvite }: Props) {
    const client = useQueryClient();
    const { friends, isLoading, isError } = useFriends();

    const invite = useMutation({
        mutationFn: async ({ id }: { id: string }) => {
            await sendInvite(id, lobby.id);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["lobby", lobby.id] });
            showToast("Lobby", "Invite sent!");
            onInvite();
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        },
    });

    if (isLoading || !friends) return <LoadingState />;
    if (isError) return <ErrorState />;

    return friends.map((friend: Profile) => {
        const isInLobby: boolean = isPlayerInLobby(friend.id, lobby);

        return (
            <UserCard
                user={friend}
                footer={
                    <Button
                        onClick={() => invite.mutate({ id: friend.id })}
                        fullWidth={true}
                        disabled={isInLobby}
                    >
                        {isInLobby ? <AcceptIcon /> : <PlusIcon />}
                    </Button>
                }
            />
        );
    });
}
