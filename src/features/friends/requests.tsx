import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import AcceptIcon from "@/components/icons/accept-icon";
import LevelIcon from "@/components/icons/level-icon";
import RejectIcon from "@/components/icons/reject-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { Items } from "@/components/layout/items";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import State from "@/components/state/state";
import TabRow from "@/components/tabs/links/row";
import { getTabs } from "@/features/friends/components/tabs";
import { useRequests } from "@/features/friends/hooks/use-requests.ts";
import {
    acceptRequest,
    rejectRequest,
} from "@/features/friends/services/requests.ts";
import type { Profile } from "@/features/profile/models/profile";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router";

export default function RequestsPage() {
    const client = useQueryClient();
    const navigate = useNavigate();
    const { requests, loading, error } = useRequests();

    const accept = useMutation({
        mutationFn: async ({ request }: { request: Profile }) =>
            await acceptRequest(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({
                queryKey: ["friends", "requests"],
            });
            await client.invalidateQueries({ queryKey: ["friends"] });
            show(Type.Friends, "Request accepted");
            navigate("/friends");
        },
        onError: (error: Error) => show(Type.Friends, error.message),
    });

    const reject = useMutation({
        mutationFn: async ({ request }: { request: Profile }) =>
            await rejectRequest(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({
                queryKey: ["friends", "requests"],
            });
            show(Type.Friends, "Request rejected");
        },
        onError: (error: Error) => show(Type.Friends, error.message),
    });

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow tabs={getTabs()} />

                <State
                    loading={loading}
                    error={error}
                    empty={requests.length === 0}
                    message="No requests"
                >
                    {requests && (
                        <Grid>
                            {requests.map((request: Profile) => (
                                <UserCard
                                    user={request}
                                    statistics={
                                        <Row responsive={false}>
                                            <Row
                                                gap={Gap.Small}
                                                items={Items.Center}
                                                responsive={false}
                                            >
                                                <LevelIcon />
                                                <p>
                                                    {request.statistics.level}
                                                </p>
                                            </Row>
                                        </Row>
                                    }
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
