import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import CancelIcon from "@/components/icons/cancel-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import TabRow from "@/components/tabs/links/row";
import showToast from "@/components/toast";
import { getTabs } from "@/features/friends/components/tabs";
import { useOutgoingRequests } from "@/features/friends/hooks/use-requests.ts";
import { cancelRequest } from "@/features/friends/services/requests.ts";
import type { Profile } from "@/features/profile/models/profile.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export default function OutgoingRequestsPage() {
    const client = useQueryClient();
    const { requests, loading, error } = useOutgoingRequests();

    const cancel = useMutation({
        mutationFn: async ({ request }: { request: Profile }) =>
            await cancelRequest(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({
                queryKey: ["friends", "outgoing"],
            });
            showToast("Request", "Cancelled");
        },
        onError: (error: Error) => {
            showToast("Request", error.message);
        },
    });

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow tabs={getTabs()} />

                <State
                    loading={loading}
                    error={error}
                    empty={requests.length === 0}
                    message="No outgoing requests"
                >
                    {requests && (
                        <Grid>
                            {requests.map((request: Profile) => (
                                <UserCard
                                    user={request}
                                    footer={
                                        <Button
                                            onClick={() =>
                                                cancel.mutate({ request })
                                            }
                                            fullWidth={true}
                                        >
                                            <CancelIcon />
                                        </Button>
                                    }
                                />
                            ))}
                        </Grid>
                    )}
                </State>
            </Column>
        </Page>
    );
}
