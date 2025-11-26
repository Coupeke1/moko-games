import Button from "@/components/buttons/button";
import CancelIcon from "@/components/icons/cancel-icon";
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
import type { Friend } from "@/models/friends/friend";
import type { Profile } from "@/models/profile/profile";
import FriendCard from "@/routes/friends/components/friend-card";
import TabRow from "@/routes/friends/components/tabs/row";
import { removeFriend, sendRequest } from "@/services/friends-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

function Add() {
    const client = useQueryClient();
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

    return (
        <section className={`grid sm:grid-cols-4 ${Gap.Medium}`}>
            <section className="sm:col-span-3">
                <Input placeholder="Search Username..." value={username} onChange={(e) => setUsername(e.target.value)} />
            </section>
            
            <Button onClick={handleAdd}>Add</Button>
        </section>
    );
}

function Friend({ friend }: { friend: Profile }) {
    const client = useQueryClient();

    const remove = useMutation({
        mutationFn: async ({ request }: { request: Profile }) => await removeFriend(request.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["friends"] });
            showToast("Request", "Removed");
        },
        onError: (error: Error) => {
            showToast("Request", error.message);
        }
    });

    function handleRemove(request: Profile) {
        remove.mutate({ request });
    };

    return (
        <FriendCard friend={friend} footer={
            <Button
                onClick={() => handleRemove(friend)}
                fullWidth={true}
            >
                <CancelIcon />
            </Button>
        } />
    );
}

export default function FriendsPage() {
    const { friends, isLoading, isError } = useFriends();

    if (isLoading || friends === undefined) return <Page><LoadingState /></Page>
    if (isError) return <Page><ErrorState /></Page>

    return (
        <Page>
            <Column gap={Gap.Large}>
                <TabRow />
                <Add />

                {
                    friends.length == 0 ? (
                        <Message>No friends :(</Message>
                    ) : (
                        <Grid>
                            {
                                friends.map((friend: Profile) => <Friend friend={friend} />)
                            }
                        </Grid>
                    )
                }
            </Column>
        </Page>
    )
}