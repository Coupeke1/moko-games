import type { Notification } from "@/features/notifications/models/notification";
import { useSocketStore } from "@/stores/socket-store.ts";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

export function watchNotifications(): Observable<Notification> {
    const client = useSocketStore.getState().client;

    const updates = client
        .watch({ destination: "/user/queue/notifications" })
        .pipe(map((msg) => JSON.parse(msg.body) as Notification));

    return updates;
}
