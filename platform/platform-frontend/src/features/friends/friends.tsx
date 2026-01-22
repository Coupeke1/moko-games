import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import CancelIcon from "@/components/icons/cancel-icon";
import ClockIcon from "@/components/icons/clock-icon";
import LevelIcon from "@/components/icons/level-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { Items } from "@/components/layout/items";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import State from "@/components/state/state";
import TabRow from "@/components/tabs/links/row";
import Add from "@/features/friends/components/add";
import { getTabs } from "@/features/friends/components/tabs";
import { useFriends } from "@/features/friends/hooks/use-friends.ts";
import type { Friend } from "@/features/friends/models/friend.ts";
import { removeFriend } from "@/features/friends/services/friends.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";

function Friend({ friend }: { friend: Friend }) {
    const client = useQueryClient();

    const remove = useMutation({
        mutationFn: async ({ friend }: { friend: Friend }) =>
            await removeFriend(friend.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["friends"] });
            show(Type.Friends, "Friend removed");
        },
        onError: (error: Error) => show(Type.Friends, error.message),
    });

    return (
        <UserCard
            user={friend}
            statistics={
                <Row responsive={false}>
                    <Row
                        gap={Gap.Small}
                        items={Items.Center}
                        responsive={false}
                    >
                        <LevelIcon />
                        <p>{friend.statistics.level}</p>
                    </Row>

                    <Row
                        gap={Gap.Small}
                        items={Items.Center}
                        responsive={false}
                    >
                        <ClockIcon />
                        <p>{friend.statistics.playTime}</p>
                    </Row>
                </Row>
            }
            footer={
                <Button
                    onClick={() => remove.mutate({ friend })}
                    fullWidth={true}
                >
                    <CancelIcon />
                </Button>
            }
        />
    );
}

export default function FriendsPage() {
    const { friends, loading, error } = useFriends();

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow tabs={getTabs()} />
                <Add />

                <State
                    loading={loading}
                    error={error}
                    empty={friends.length === 0}
                    message="No friends"
                >
                    {friends && (
                        <Grid>
                            {friends.map((friend: Friend) => (
                                <Friend key={friend.id} friend={friend} />
                            ))}
                        </Grid>
                    )}
                </State>
            </Column>
        </Page>
    );
}
