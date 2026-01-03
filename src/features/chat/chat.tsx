import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import BotCard from "@/features/chat/components/channel-card/bot-card";
import FriendsSection from "@/features/chat/components/friends";
import MessagesSection from "@/features/chat/components/messages";
import SendBar from "@/features/chat/components/send-bar";
import { useChannel } from "@/features/chat/hooks/use-channel";
import { sendPrivateMessage } from "@/features/chat/services/chat";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";

export default function ChatPage() {
    const client = useQueryClient();
    const [selected, setSelected] = useState<string>("bot");
    const { channel, loading, error } = useChannel(selected);

    useEffect(() => {
        client.invalidateQueries({ queryKey: ["channels", selected] });
    }, [selected, client]);

    const save = useMutation({
        mutationFn: async ({
            channel,
            message,
        }: {
            channel: string;
            message: string;
        }) => await sendPrivateMessage(channel, message),
        onSuccess: async () => {
            await client.refetchQueries({
                queryKey: ["channel", channel, "messages"],
            });
        },
        onError: (error: Error) => show(Type.Chat, error.message),
    });

    return (
        <Page>
            <section className={`grid md:grid-cols-12 ${Gap.Medium}`}>
                <section className="col-span-12 md:col-span-4 h-full rounded-lg flex flex-col gap-2 md:h-[calc(100vh-12rem)] overflow-y-scroll">
                    <BotCard
                        selected={selected === "bot"}
                        onSelect={() => setSelected("bot")}
                    />

                    <FriendsSection
                        selected={selected}
                        onSelect={(id: string) => setSelected(id)}
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
                                save.mutate({ channel: channel.id, message })
                            }
                        />
                    )}
                </section>
            </section>
        </Page>
    );
}
