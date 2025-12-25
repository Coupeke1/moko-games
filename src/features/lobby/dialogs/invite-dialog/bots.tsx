import Button from "@/components/buttons/button";
import BotCard from "@/components/cards/bot-card";
import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import AcceptIcon from "@/components/icons/accept-icon";
import PlusIcon from "@/components/icons/plus-icon";
import type { Lobby } from "@/features/lobby/models/lobby.ts";
import { addBot } from "@/features/lobby/services/bots.ts";
import type { Bot } from "@/features/profiles/models/bot.ts";
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
            show(Type.Lobby, "Bot added");
            onInvite();
        },
        onError: (error: Error) => show(Type.Lobby, error.message),
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
