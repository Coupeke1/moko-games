import MessagesSection from "@/components/chat/messages";
import SendBar from "@/components/chat/send-bar";
import show from "@/components/global/toast/toast";
import { Type as ToastType } from "@/components/global/toast/type";
import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import BotsSection from "@/features/chat/components/bots";
import FriendsSection from "@/features/chat/components/friends";
import { useChannel } from "@/features/chat/hooks/use-channel";
import { Type } from "@/features/chat/models/channel/type";
import { sendMessage } from "@/features/chat/services/chat";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";

export default function ChatPage() {
    const client = useQueryClient();
    const [selected, setSelected] = useState<{ id: string | null; type: Type }>(
        { id: null, type: Type.Bot },
    );
    const { channel, loading, error } = useChannel(selected.id, selected.type);

    useEffect(() => {
        client.invalidateQueries({ queryKey: ["channels", selected] });
    }, [selected, client]);

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

    return (
        <Page>
            <section className={`grid md:grid-cols-12 ${Gap.Medium}`}>
                <section className="col-span-12 md:col-span-4 h-full rounded-lg flex flex-col gap-2 md:h-[calc(100vh-12rem)] overflow-y-scroll">
                    <BotsSection
                        selected={selected.id}
                        onSelect={(id: string) =>
                            setSelected({ id, type: Type.Bot })
                        }
                    />

                    <FriendsSection
                        selected={selected.id}
                        onSelect={(id: string) =>
                            setSelected({ id, type: Type.Friends })
                        }
                    />
                </section>

                <section
                    className={`flex flex-col ${Gap.Medium} col-span-12 md:col-span-8`}
                >
                    <section className="p-4 rounded-lg bg-bg-3 flex flex-col gap-8 h-64 md:h-[calc(100vh-15rem)] min-h-0">
                        <State
                            loading={loading}
                            error={error}
                            empty={!channel}
                            message="No channel"
                        >
                            <section className="h-full min-h-0 flex">
                                {channel && (
                                    <MessagesSection channel={channel} />
                                )}
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
                </section>
            </section>
        </Page>
    );
}
