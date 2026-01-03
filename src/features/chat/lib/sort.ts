import type { Message } from "@/features/chat/models/message";

export function byDate(messages: Message[]) {
    return [...messages].sort(
        (a, b) =>
            new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime(),
    );
}
