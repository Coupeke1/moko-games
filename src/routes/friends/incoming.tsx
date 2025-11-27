import Button from "@/components/buttons/button";
import AcceptIcon from "@/components/icons/accept-icon";
import RejectIcon from "@/components/icons/reject-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import showToast from "@/components/toast";
import { useIncomingRequests } from "@/hooks/use-requests";
import type { Profile } from "@/models/profile/profile";
import FriendCard from "@/routes/friends/components/friend-card";
import TabRow from "@/components/tabs/links/row";
import { acceptRequest, rejectRequest } from "@/services/friends-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { getTabs } from "@/routes/friends/components/tabs";

export default function IncomingRequestsPage() {
    const client = useQueryClient();
    const { requests, isLoading, isError } = useIncomingRequests();

    const accept = useMutation({
        mutationFn: async ({ request }: { request: Profile }) => await acceptRequest(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["friends", "incoming"] });
            await client.invalidateQueries({ queryKey: ["friends"] });
            showToast("Request", "Accepted");
        },
        onError: (error: Error) => {
            showToast("Request", error.message);
        }
    });

    function handleAccept(request: Profile) {
        accept.mutate({ request });
    };

    const reject = useMutation({
        mutationFn: async ({ request }: { request: Profile }) => await rejectRequest(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["friends", "incoming"] });
            showToast("Request", "Rejected");
        },
        onError: (error: Error) => {
            showToast("Request", error.message);
        }
    });

    function handleReject(request: Profile) {
        reject.mutate({ request });
    };

    if (isLoading || requests === undefined) return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow tabs={getTabs()} />
                <LoadingState />
            </Column>
        </Page>
    );

    if (isError) return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow tabs={getTabs()} />
                <ErrorState />
            </Column>
        </Page>
    );

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow tabs={getTabs()} />

                {
                    requests.length == 0 ? (
                        <Message>No incoming requests :(</Message>
                    ) : (
                        <Grid>
                            {
                                requests.map((request: Profile) => (
                                    <FriendCard friend={request} footer={
                                        <Row>
                                            <Button
                                                onClick={() => handleAccept(request)}
                                                fullWidth={true}
                                            >
                                                <AcceptIcon />
                                            </Button>
                                            <Button
                                                onClick={() => handleReject(request)}
                                                fullWidth={true}
                                            >
                                                <RejectIcon />
                                            </Button>
                                        </Row>
                                    } />
                                ))
                            }
                        </Grid>
                    )
                }
            </Column>
        </Page>
    )
}