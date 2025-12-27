import type { Message } from "@/features/chat/models/message";
import type { Friend } from "@/features/friends/models/friend";
import type { Bot } from "@/features/profiles/models/bot";

interface Props {
    sender: Friend | Bot;
    message: Message;
}

export default function Message({ sender, message }: Props) {
    return (
        <article className="flex flex-col">
            <section className="flex flex-row items-center justify-between gap-2">
                <p className="font-bold truncate">{sender.username}</p>
                <p className="text-sm text-fg-2">{message.timestamp}</p>
            </section>

            <p className="text-fg-2">{message.content}</p>
        </article>
    );
}
