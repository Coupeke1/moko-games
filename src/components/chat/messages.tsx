import Message from "@/components/chat/message";
import { Gap } from "@/components/layout/gap";
import State from "@/components/state/state";
import { useMessage } from "@/features/chat/hooks/use-message";
import { useMessages } from "@/features/chat/hooks/use-messages";
import type { Channel } from "@/features/chat/models/channel/channel";
import type { Message as MessageType } from "@/features/chat/models/message";
import { getSender } from "@/features/chat/services/chat";

interface Props {
    channel: Channel;
}

export default function MessagesSection({ channel }: Props) {
    const { messages, loading, error } = useMessages(channel.id, channel.type);
    useMessage(channel.id);

    return (
        <State loading={loading} error={error} empty={messages.length === 0}>
            <section
                className={`flex flex-col ${Gap.ExtraLarge} overflow-y-auto w-full h-full min-h-0`}
            >
                {messages.map((message: MessageType) => (
                    <Message
                        key={message.id}
                        message={message}
                        sender={getSender(channel, message)}
                    />
                ))}
            </section>
        </State>
    );
}
