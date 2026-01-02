import type { Message } from "@/features/chat/models/message";
import { useSocketStore } from "@/stores/socket-store.ts";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

export function watchChat(id: string): Observable<Message> {
    const client = useSocketStore.getState().client;

    const updates = client
        .watch({ destination: `/user/queue/chat-${id}` })
        .pipe(map((msg) => JSON.parse(msg.body) as Message));

    return updates;
}
