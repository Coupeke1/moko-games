import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import Row from "@/components/layout/row";
import type { User } from "@/features/chat/models/channel/user";
import type { Message } from "@/features/chat/models/message";
import type { Bot } from "@/features/profiles/models/bot";
import { relativeDate } from "@/lib/format";

interface Props {
    sender: User | Bot;
    message: Message;
}

export default function Message({ sender, message }: Props) {
    return (
        <article className="flex flex-col">
            <Row
                items={Items.Center}
                justify={Justify.Between}
                responsive={false}
            >
                <p className="font-bold truncate text-lg">{sender.username}</p>
                <p className="text text-fg-2 text-sm">
                    {relativeDate(message.timestamp)}
                </p>
            </Row>

            <p className="text-fg-2">{message.content}</p>
        </article>
    );
}
