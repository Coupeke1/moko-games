import Button from "@/components/buttons/button";
import Card from "@/components/cards/user-card";
import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import AcceptIcon from "@/components/icons/accept-icon";
import CancelIcon from "@/components/icons/cancel-icon";
import RejectIcon from "@/components/icons/reject-icon";
import StarIcon from "@/components/icons/star-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import type { Lobby } from "@/features/lobby/models/lobby.ts";
import type { Player } from "@/features/lobby/models/player.ts";
import { isUserOwner } from "@/features/lobby/services/lobby.ts";
import {
    readyPlayer,
    removePlayer,
    unReadyPlayer,
} from "@/features/lobby/services/players";
import type { Profile } from "@/features/profile/models/profile.ts";
import { useMutation } from "@tanstack/react-query";

export default function PlayerCard({
    player,
    lobby,
    profile,
    isOwner,
}: {
    player: Player;
    lobby: Lobby;
    profile: Profile;
    isOwner: boolean;
}) {
    const remove = useMutation({
        mutationFn: async () => await removePlayer(player.id, lobby.id),
        onError: (error: Error) => show(Type.Lobby, error.message),
    });

    const ready = useMutation({
        mutationFn: async () => await readyPlayer(lobby.id),
        onError: (error: Error) => show(Type.Lobby, error.message),
    });

    const unReady = useMutation({
        mutationFn: async () => await unReadyPlayer(lobby.id),
        onError: (error: Error) => show(Type.Lobby, error.message),
    });

    function PlayerButton() {
        const isCurrentPlayer: boolean = player.id === profile.id;
        const playerIsOwner: boolean = isUserOwner(player.id, lobby);

        if (isCurrentPlayer) {
            return (
                <Button
                    onClick={player.ready ? unReady.mutate : ready.mutate}
                    fullWidth={true}
                >
                    {player.ready ? <AcceptIcon /> : <RejectIcon />}
                </Button>
            );
        }

        if (isOwner && !playerIsOwner) {
            return (
                <Button onClick={remove.mutate} fullWidth={true}>
                    <CancelIcon />
                </Button>
            );
        }

        if (playerIsOwner) {
            return (
                <Button disabled={true} fullWidth={true}>
                    <StarIcon />
                </Button>
            );
        }

        return (
            <Button disabled={true} fullWidth={true}>
                {player.ready ? <AcceptIcon /> : <RejectIcon />}
            </Button>
        );
    }

    return (
        <Card
            user={player}
            statistics={
                <Row gap={Gap.Small} items={Items.Center} responsive={false}>
                    {player.ready ? <AcceptIcon /> : <RejectIcon />}
                    {player.ready ? "Ready" : "Not Ready"}
                </Row>
            }
            footer={PlayerButton()}
        />
    );
}
