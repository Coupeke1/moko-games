import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import AcceptIcon from "@/components/icons/accept-icon";
import PlusIcon from "@/components/icons/plus-icon";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import { useFriends } from "@/features/friends/hooks/use-friends.ts";
import type { Lobby } from "@/features/lobby/models/lobby.ts";
import { sendInvite } from "@/features/lobby/services/invites.ts";
import { isPlayerInLobby } from "@/features/lobby/services/players.ts";
import type { Profile } from "@/features/profile/models/profile.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface Props {
    lobby: Lobby;
    onInvite: () => void;
}

export default function FriendsSection({ lobby, onInvite }: Props) {
    const client = useQueryClient();
    const { friends, loading: isLoading, error: isError } = useFriends();

    const invite = useMutation({
        mutationFn: async ({ id }: { id: string }) => {
            await sendInvite(id, lobby.id);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["lobby", lobby.id] });
            show(Type.Lobby, "Invite sent!");
            onInvite();
        },
        onError: (error: Error) => show(Type.Lobby, error.message),
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
