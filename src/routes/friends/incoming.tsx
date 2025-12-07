import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import AcceptIcon from "@/components/icons/accept-icon";
import RejectIcon from "@/components/icons/reject-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import TabRow from "@/components/tabs/links/row";
import showToast from "@/components/toast";
import { useIncomingRequests } from "@/hooks/use-requests";
import type { Profile } from "@/models/profile/profile";
import { getTabs } from "@/routes/friends/components/tabs";
import { acceptRequest, rejectRequest } from "@/services/friends-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export default function IncomingRequestsPage() {
    const client = useQueryClient();
    const { requests, loading, error } = useIncomingRequests();

    const accept = useMutation({
        mutationFn: async ({ request }: { request: Profile }) =>
            await acceptRequest(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({
                queryKey: ["friends", "incoming"],
            });
            await client.invalidateQueries({ queryKey: ["friends"] });
            showToast("Request", "Accepted");
        },
        onError: (error: Error) => {
            showToast("Request", error.message);
        },
    });

    const reject = useMutation({
        mutationFn: async ({ request }: { request: Profile }) =>
            await rejectRequest(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({
                queryKey: ["friends", "incoming"],
            });
            showToast("Request", "Rejected");
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
                        <ErrorState>No incoming requests</ErrorState>
                    ) : (
                        <Grid>
                            {requests.map((request: Profile) => (
                                <UserCard
                                    user={request}
                                    footer={
                                        <Row>
                                            <Button
                                                onClick={() =>
                                                    accept.mutate({ request })
                                                }
                                                fullWidth={true}
                                            >
                                                <AcceptIcon />
                                            </Button>
                                            <Button
                                                onClick={() =>
                                                    reject.mutate({ request })
                                                }
                                                fullWidth={true}
                                            >
                                                <RejectIcon />
                                            </Button>
                                        </Row>
                                    }
                                />
                            ))}
                        </Grid>
                    ))}
            </Column>
        </Page>
    );
}
