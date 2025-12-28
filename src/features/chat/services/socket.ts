import type { Message } from "@/features/chat/models/message";
import { useSocketStore } from "@/stores/socket-store.ts";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

export function watchChat(): Observable<Message> {
    const client = useSocketStore.getState().client;

    const updates = client
        .watch({ destination: "/user/queue/chat" })
        .pipe(map((msg) => JSON.parse(msg.body)));

    return updates;
}
