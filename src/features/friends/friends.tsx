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
import Add from "@/features/friends/components/add";
import { getTabs } from "@/features/friends/components/tabs";
import { useFriends } from "@/features/friends/hooks/use-friends.ts";
import type { Friend } from "@/features/friends/models/friend.ts";
import { removeFriend } from "@/features/friends/services/friends.ts";
import type { Profile } from "@/features/profile/models/profile.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";

function Friend({ friend }: { friend: Profile }) {
    const client = useQueryClient();

    const remove = useMutation({
        mutationFn: async ({ friend }: { friend: Profile }) =>
            await removeFriend(friend.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["friends"] });
            showToast(friend.username, "Removed");
        },
        onError: (error: Error) => {
            showToast(friend.username, error.message);
        },
    });

    return (
        <UserCard
            user={friend}
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

                <State data={friends} loading={loading} error={error} />

                {friends &&
                    (friends.length == 0 ? (
                        <ErrorState>No friends</ErrorState>
                    ) : (
                        <Grid>
                            {friends.map((friend: Profile) => (
                                <Friend key={friend.id} friend={friend} />
                            ))}
                        </Grid>
                    ))}
            </Column>
        </Page>
    );
}
