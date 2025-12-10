import Column from "@/components/layout/column";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import { useInvites } from "@/features/invites/hooks/use-invites";
import type { Lobby } from "@/features/lobby/models/lobby";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Link as RouterLink, useNavigate } from "react-router";
import { acceptInvite } from "../lobby/services/invites";
import showToast from "@/components/toast";

const game = "30c50910-7b97-430b-ae6d-8b8a9b40c66f";

export default function InvitesPage() {
    const client = useQueryClient();
    const navigate = useNavigate();
    const { invites, loading, error } = useInvites(game);

    const accept = useMutation({
        mutationFn: async ({ invite }: { invite: Lobby }) =>
            await acceptInvite(invite.id),
        onSuccess: async (_data, variables) => {
            await client.invalidateQueries({
                queryKey: ["lobby", "invites", game],
            });
            showToast("Invite", "Accepted");
            navigate(`/lobbies/${variables.invite.id}`);
        },
        onError: (error: Error) => {
            showToast("Invite", error.message);
        },
    });

    return (
        <Page>
            <State data={invites} loading={loading} error={error} />

            {invites &&
                (invites.length == 0 ? (
                    <ErrorState>No invites</ErrorState>
                ) : (
                    <Column>
                        {invites.map((invite: Lobby) => (
                            <button onClick={() => accept.mutate({ invite })}>
                                {invite.id}
                            </button>
                        ))}
                    </Column>
                ))}
        </Page>
    );
}
