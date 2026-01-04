import MessagesSection from "@/components/chat/messages";
import SendBar from "@/components/chat/send-bar";
import show from "@/components/global/toast/toast";
import { Type as ToastType } from "@/components/global/toast/type";
import Column from "@/components/layout/column";
import State from "@/components/state/state";
import { useChannel } from "@/features/chat/hooks/use-channel";
import { Type } from "@/features/chat/models/channel/type";
import { sendMessage } from "@/features/chat/services/chat";
import type { Lobby } from "@/features/lobby/models/lobby";
import { useMutation, useQueryClient } from "@tanstack/react-query";

interface Props {
    lobby: Lobby;
}

export default function Chat({ lobby }: Props) {
    const client = useQueryClient();
    const { channel, loading, error } = useChannel(lobby.id, Type.Lobby);

    const send = useMutation({
        mutationFn: async ({
            channel,
            type,
            message,
        }: {
            channel: string;
            type: Type;
            message: string;
        }) => await sendMessage(channel, type, message),
        onSuccess: async () => {
            await client.refetchQueries({
                queryKey: ["channel", channel, "messages"],
            });
        },
        onError: (error: Error) => show(ToastType.Chat, error.message),
    });

    if (!channel || loading || error) return null;
    return (
        <Column>
            <section className="p-4 rounded-lg bg-bg-3 flex flex-col gap-8 h-72 min-h-0">
                <State
                    loading={loading}
                    error={error}
                    empty={!channel}
                    message="No channel"
                >
                    <section className="h-full min-h-0 flex">
                        {channel && <MessagesSection channel={channel} />}
                    </section>
                </State>
            </section>

            {channel && (
                <SendBar
                    onSend={(message: string) =>
                        send.mutate({
                            channel: channel.id,
                            type: channel.type,
                            message,
                        })
                    }
                />
            )}
        </Column>
    );
}
