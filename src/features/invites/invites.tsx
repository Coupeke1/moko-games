import Column from "@/components/layout/column";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import { useInvites } from "@/features/invites/hooks/use-invites";
import type { Lobby } from "@/features/lobby/models/lobby";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate, useParams } from "react-router";
import { acceptInvite } from "@/features/lobby/services/invites";
import showToast from "@/components/toast";
import { useEffect } from "react";

export default function InvitesPage() {
    const client = useQueryClient();
    const navigate = useNavigate();
    const params = useParams();
    const id = params.id;

    useEffect(() => {
        if (!id) navigate("/library");
    }, [id, navigate]);

    const { invites, loading, error } = useInvites(id!);

    const accept = useMutation({
        mutationFn: async ({ invite }: { invite: Lobby }) =>
            await acceptInvite(invite.id),
        onSuccess: async (_data, variables) => {
            await client.invalidateQueries({
                queryKey: ["lobby", "invites", id],
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
