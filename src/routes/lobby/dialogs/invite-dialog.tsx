import Button from "@/components/buttons/button";
import FriendCard from "@/components/cards/friend-card";
import Dialog from "@/components/dialog/dialog";
import PlusIcon from "@/components/icons/plus-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import Message from "@/components/state/message";
import TabContent from "@/components/tabs/buttons/content";
import TabRow from "@/components/tabs/buttons/row";
import showToast from "@/components/toast";
import { useFriends } from "@/hooks/use-friends";
import type { Lobby } from "@/models/lobby/lobby";
import type { Profile } from "@/models/profile/profile";
import { isPlayerInLobby, sendInvite } from "@/services/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router";

interface Props {
    lobby: Lobby;
    open: boolean;
    onChange: (open: boolean) => void;
}

function Friends({ lobby }: { lobby: Lobby }) {
    const client = useQueryClient();
    const { friends, isLoading, isError } = useFriends();

    const invite = useMutation({
        mutationFn: async ({ id }: { id: string; }) => {
            await sendInvite(id, lobby.id);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["lobby", lobby.id] });
            showToast("Lobby", "Invite sent!");
            close();
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        }
    });

    function handleInvite(id: string) {
        invite.mutate({ id });
    };

    if (isLoading || friends === undefined) return <LoadingState />
    if (isError) return <ErrorState />

    return friends.length == 0 ? (
        <Message>No friends :(</Message>
    ) : (
        <Grid>
            {
                friends.map((friend: Profile) => {
                    const isInLobby: boolean = isPlayerInLobby(friend.id, lobby);

                    return (
                        <FriendCard friend={friend} footer={
                            <Button
                                onClick={() => handleInvite(friend.id)}
                                fullWidth={true}
                                disabled={isInLobby}
                            >
                                <PlusIcon />
                            </Button>
                        } />
                    )
                })
            }
        </Grid>
    )
}

export default function InviteDialog({ lobby, open, onChange }: Props) {
    const navigate = useNavigate();

    const [current, setCurrent] = useState<string>("Friends");

    return (
        <Dialog title="Invite" onClose={() => setCurrent("Friends")} open={open} onChange={onChange}>
            <Column gap={Gap.Large}>
                <TabRow
                    tabs={["Friends", "Bots"]}
                    current={current}
                    setCurrent={setCurrent}
                />

                <TabContent
                    current={current}
                    tabs={[{
                        title: "Friends", element: <Friends lobby={lobby} />
                    },
                    {
                        title: "Bots", element: (
                            <Column>
                                <p>Bots</p>
                            </Column>
                        )
                    }]}
                />
            </Column>
        </Dialog>
    );
}