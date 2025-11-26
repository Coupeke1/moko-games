import Button from "@/components/buttons/button";
import CancelIcon from "@/components/icons/cancel-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import showToast from "@/components/toast";
import { useOutgoingRequests } from "@/hooks/use-requests";
import type { Profile } from "@/models/profile/profile";
import FriendCard from "@/routes/friends/components/friend-card";
import TabRow from "@/routes/friends/components/tabs/row";
import { cancelRequest } from "@/services/friends-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export default function OutgoingRequestsPage() {
    const client = useQueryClient();
    const { requests, isLoading, isError } = useOutgoingRequests();

    const cancel = useMutation({
        mutationFn: async ({ request }: { request: Profile }) => await cancelRequest(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["friends", "outgoing"] });
            showToast("Request", "Cancelled");
        },
        onError: (error: Error) => {
            showToast("Request", error.message);
        }
    });

    function handleCancel(request: Profile) {
        cancel.mutate({ request });
    };

    if (isLoading || requests === undefined) return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow />
                <LoadingState />
            </Column>
        </Page>
    );

    if (isError) return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow />
                <ErrorState />
            </Column>
        </Page>
    );

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow />

                {
                    requests.length == 0 ? (
                        <Message>No outgoing requests :(</Message>
                    ) : (
                        <Grid>
                            {
                                requests.map((request: Profile) => (
                                    <FriendCard friend={request} footer={
                                        <Button
                                            onClick={() => handleCancel(request)}
                                            fullWidth={true}
                                        >
                                            <CancelIcon />
                                        </Button>
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