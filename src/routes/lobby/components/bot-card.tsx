import Button from "@/components/buttons/button";
import Card from "@/components/cards/bot-card";
import BotIcon from "@/components/icons/bot-icon";
import CancelIcon from "@/components/icons/cancel-icon";
import showToast from "@/components/toast";
import type { Lobby } from "@/models/lobby/lobby";
import type { Bot } from "@/models/profile/bot";
import { removeBot } from "@/services/lobby/lobby-service";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export default function BotCard({
    bot,
    lobby,
    isOwner,
}: {
    bot: Bot;
    lobby: Lobby;
    isOwner: boolean;
}) {
    const client = useQueryClient();

    const remove = useMutation({
        mutationFn: async () => await removeBot(lobby.id),
        onSuccess: async () => {
            await client.invalidateQueries({ queryKey: ["lobby", lobby.id] });
            showToast(bot.username, "Removed");
        },
        onError: (error: Error) => showToast(bot.username, error.message),
    });

    function BotButton() {
        if (isOwner) {
            return (
                <Button onClick={remove.mutate} fullWidth={true}>
                    <CancelIcon />
                </Button>
            );
        }

        return (
            <Button disabled={true} fullWidth={true}>
                <BotIcon />
            </Button>
        );
    }

    return <Card bot={bot} footer={BotButton()} />;
}
