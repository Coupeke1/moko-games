import Message from "@/components/chat/message";
import { Gap } from "@/components/layout/gap";
import State from "@/components/state/state";
import { useMessage } from "@/features/chat/hooks/use-message";
import { useMessages } from "@/features/chat/hooks/use-messages";
import type { Channel } from "@/features/chat/models/channel/channel";
import type { Message as MessageType } from "@/features/chat/models/message";
import { getSender } from "@/features/chat/services/chat";
import { useEffect, useRef, useState } from "react";

interface Props {
    channel: Channel;
}

export default function MessagesSection({ channel }: Props) {
    const { messages, loading, error } = useMessages(channel.id, channel.type);
    useMessage(channel.id);

    const container = useRef<HTMLElement | null>(null);
    const anchor = useRef<HTMLDivElement | null>(null);
    const [scroll, setScroll] = useState(true);

    const handleScroll = () => {
        const element = container.current;
        if (!element) return;

        const threshold = 80;

        setScroll(
            element.scrollHeight - element.scrollTop - element.clientHeight <
                threshold,
        );
    };

    useEffect(() => {
        if (!scroll) return;
        anchor.current?.scrollIntoView({ behavior: "auto" });
    }, [messages, scroll]);

    return (
        <State loading={loading} error={error} empty={messages.length === 0}>
            <section
                ref={container}
                onScroll={handleScroll}
                className={`flex flex-col ${Gap.ExtraLarge} overflow-y-auto w-full h-full min-h-0`}
            >
                {messages.map((message: MessageType) => (
                    <Message
                        key={message.id}
                        message={message}
                        sender={getSender(channel, message)}
                    />
                ))}
                <div ref={anchor} />
            </section>
        </State>
    );
}
