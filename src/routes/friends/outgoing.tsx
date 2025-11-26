import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import { useOutgoingRequests } from "@/hooks/use-requests";
import type { Friend } from "@/models/friends/friend";
import TabRow from "@/routes/friends/components/tabs/row";

export default function OutgoingRequestsPage() {
    const { requests, isLoading, isError } = useOutgoingRequests();

    if (isLoading || requests === undefined) return <Page><LoadingState /></Page>
    if (isError) return <Page><ErrorState /></Page>

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow />

                {
                    requests.length == 0 ? (
                        <Message>No outgoing requests :(</Message>
                    ) : (
                        requests.map((friend: Friend) => (
                            <p key={friend.id}>{friend.username}</p>
                        ))
                    )
                }
            </Column>
        </Page>
    )
}