import Button from "@/components/buttons/button";
import Input from "@/components/inputs/input";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import showToast from "@/components/toast";
import { useFriends } from "@/hooks/use-friends";
import type { Profile } from "@/models/profile/profile";
import TabRow from "@/routes/friends/components/tabs/row";
import FriendCard from "@/routes/profile/components/friend-card";
import { sendRequest } from "@/services/friends-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

export default function FriendsPage() {
    const client = useQueryClient();
    const { friends, isLoading, isError } = useFriends();

    const [username, setUsername] = useState("");

    const add = useMutation({
        mutationFn: async ({ username }: { username: string }) => {
            await sendRequest(username);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["friends"] });
            showToast(username, "Request sent");
            close();
        },
        onError: (error: Error) => {
            showToast(username, error.message);
        }
    });

    function handleAdd() {
        add.mutate({ username });
    };

    if (isLoading || friends === undefined) return <Page><LoadingState /></Page>
    if (isError) return <Page><ErrorState /></Page>

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow />

                <section className={`grid sm:grid-cols-4 ${Gap.Medium}`}>
                    <section className="sm:col-span-3">
                        <Input placeholder="Search Username..." value={username} onChange={(e) => setUsername(e.target.value)} />
                    </section>
                    <Button onClick={handleAdd}>Add</Button>
                </section>

                {
                    friends.length == 0 ? (
                        <Message>No friends :(</Message>
                    ) : (
                        <Grid>
                            {

                                friends.map((friend: Profile) => (
                                    <FriendCard
                                        image={friend.image}
                                        username={friend.username}
                                        level={friend.statistics.level}
                                        playtime={friend.statistics.playTime.toString()}
                                    />
                                ))
                            }
                        </Grid>
                    )
                }
            </Column>
        </Page>
    )
}