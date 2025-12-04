import Button from "@/components/buttons/button";
import AcceptIcon from "@/components/icons/accept-icon";
import Column from "@/components/layout/column";
import Grid from "@/components/layout/grid/grid";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import showToast from "@/components/toast";
import { useInvites } from "@/hooks/use-invite";
import type { Game } from "@/models/library/game";
import type { Lobby } from "@/models/lobby/lobby";
import InviteCard from "@/routes/library/components/invite-card";
import { acceptInvite } from "@/services/lobby/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface Props {
    game: Game;
}

export function Invites({ game }: Props) {
    const client = useQueryClient();
    const { invites, isLoading, isError } = useInvites(game.id);

    const accept = useMutation({
        mutationFn: async ({ invite }: { invite: Lobby }) =>
            await acceptInvite(invite.id),
        onSuccess: async (_data, variables) => {
            await client.invalidateQueries({
                queryKey: ["lobby", "invites", game.id],
            });
            showToast("Invite", "Accepted");
            window.location.replace(`/lobby/${variables.invite.id}`);
        },
        onError: (error: Error) => {
            showToast("Invite", error.message);
        },
    });

    function handleAccept(invite: Lobby) {
        accept.mutate({ invite });
    }

    if (isLoading || !invites) return <LoadingState />;
    if (isError) return <ErrorState />;

    return (
        <Column>
            {invites.length == 0 ? (
                <Message>No invites :(</Message>
            ) : (
                <Grid>
                    {invites.map((lobby: Lobby) => (
                        <InviteCard
                            key={lobby.id}
                            lobby={lobby}
                            footer={
                                <Button
                                    onClick={() => handleAccept(lobby)}
                                    fullWidth={true}
                                >
                                    <AcceptIcon />
                                </Button>
                            }
                        />
                    ))}
                </Grid>
            )}
        </Column>
    );
}
