import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import AcceptIcon from "@/components/icons/accept-icon";
import RejectIcon from "@/components/icons/reject-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import State from "@/components/state/state";
import TabRow from "@/components/tabs/links/row";
import showToast from "@/components/toast";
import { getTabs } from "@/features/friends/components/tabs";
import { useIncomingRequests } from "@/features/friends/hooks/use-requests.ts";
import {
    acceptRequest,
    rejectRequest,
} from "@/features/friends/services/requests.ts";
import type { Profile } from "@/features/profile/models/profile.ts";
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

                <State
                    loading={loading}
                    error={error}
                    empty={requests.length === 0}
                    message="No incoming requests"
                >
                    {requests && (
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
                    )}
                </State>
            </Column>
        </Page>
    );
}
