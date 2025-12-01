import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import CancelIcon from "@/components/icons/cancel-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import TabRow from "@/components/tabs/links/row";
import showToast from "@/components/toast";
import { useOutgoingRequests } from "@/hooks/use-requests";
import type { Profile } from "@/models/profile/profile";
import { getTabs } from "@/routes/friends/components/tabs";
import { cancelRequest } from "@/services/friends-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export default function OutgoingRequestsPage() {
    const client = useQueryClient();
    const { requests, isLoading, isError } = useOutgoingRequests();

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

    if (isLoading || requests === undefined)
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
                    <Message>No outgoing requests :(</Message>
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
                )}
            </Column>
        </Page>
    );
}
