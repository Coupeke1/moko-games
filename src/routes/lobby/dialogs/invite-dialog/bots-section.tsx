import Button from "@/components/buttons/button";
import BotCard from "@/components/cards/bot-card";
import AcceptIcon from "@/components/icons/accept-icon";
import PlusIcon from "@/components/icons/plus-icon";
import showToast from "@/components/toast";
import type { Lobby } from "@/models/lobby/lobby";
import type { Bot } from "@/models/profile/bot";
import { addBot } from "@/services/lobby/lobby.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface Props {
    lobby: Lobby;
    onInvite: () => void;
}

export default function BotsSection({ lobby, onInvite }: Props) {
    const client = useQueryClient();

    const invite = useMutation({
        mutationFn: async () => await addBot(lobby.id),
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["lobby", lobby.id] });
            showToast("Lobby", "Added bot!");
            onInvite();
        },
        onError: (error: Error) => {
            showToast("Lobby", error.message);
        },
    });

    return (
        <BotCard
            bot={
                {
                    id: "bot",
                    username: "Bot",
                    image: "",
                } as Bot
            }
            footer={
                lobby.bot ? (
                    <Button disabled={true} fullWidth={true}>
                        <AcceptIcon />
                    </Button>
                ) : (
                    <Button onClick={() => invite.mutate()} fullWidth={true}>
                        <PlusIcon />
                    </Button>
                )
            }
        />
    );
}
