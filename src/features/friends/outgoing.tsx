import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import CancelIcon from "@/components/icons/cancel-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import TabRow from "@/components/tabs/links/row";
import showToast from "@/components/toast";
import { useOutgoingRequests } from "@/features/friends/hooks/use-requests.ts";
import type { Profile } from "@/features/profile/models/profile.ts";
import { getTabs } from "@/features/friends/components/tabs";
import { cancelRequest } from "@/features/friends/services/requests.ts";
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
                <State data={requests} loading={loading} error={error} />

                {requests &&
                    (requests.length == 0 ? (
                        <ErrorState>No outgoing requests</ErrorState>
                    ) : (
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
                    ))}
            </Column>
        </Page>
    );
}
