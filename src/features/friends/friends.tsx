import Button from "@/components/buttons/button";
import UserCard from "@/components/cards/user-card";
import CancelIcon from "@/components/icons/cancel-icon";
import Input from "@/components/inputs/input";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import TabRow from "@/components/tabs/links/row";
import showToast from "@/components/toast";
import { useFriends } from "@/features/friends/hooks/use-friends.ts";
import type { Friend } from "@/features/friends/models/friend.ts";
import type { Profile } from "@/features/profile/models/profile.ts";
import { getTabs } from "@/features/friends/components/tabs";
import { removeFriend } from "@/features/friends/services/friends.ts";
import { sendRequest } from "@/features/friends/services/requests.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

function Add() {
    const client = useQueryClient();
    const [username, setUsername] = useState("");

    const add = useMutation({
        mutationFn: async ({ username }: { username: string }) =>
            await sendRequest(username),
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["friends"] });
            showToast(username, "Request sent");
            close();
        },
        onError: (error: Error) => {
            showToast("Friends", error.message);
        },
    });

    return (
        <section className={`grid sm:grid-cols-4 ${Gap.Medium}`}>
            <section className="sm:col-span-3">
                <Input
                    placeholder="Search Username..."
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </section>

            <Button onClick={() => add.mutate({ username })}>Add</Button>
        </section>
    );
}

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
