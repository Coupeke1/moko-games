import Button from "@/components/buttons/button";
import AcceptIcon from "@/components/icons/accept-icon";
import ClockIcon from "@/components/icons/clock-icon";
import LevelIcon from "@/components/icons/level-icon";
import RejectIcon from "@/components/icons/reject-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { Items } from "@/components/layout/items";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import TabRow from "@/components/tabs/links/row";
import showToast from "@/components/toast";
import UserCard from "@/components/cards/user-card";
import { useIncomingRequests } from "@/hooks/use-requests";
import type { Profile } from "@/models/profile/profile";
import { getTabs } from "@/routes/friends/components/tabs";
import { acceptRequest, rejectRequest } from "@/services/friends-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export default function IncomingRequestsPage() {
    const client = useQueryClient();
    const { requests, isLoading, isError } = useIncomingRequests();

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

    if (isLoading || !requests)
        return (
            <Page>
                <Column gap={Gap.Large}>
                    <TabRow tabs={getTabs()} />
                    <LoadingState />
                </Column>
            </Page>
        );

    if (isError)
        return (
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

                {requests.length == 0 ? (
                    <Message>No incoming requests :(</Message>
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
                )}
            </Column>
        </Page>
    );
}
