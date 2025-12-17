import type { Notification } from "@/features/notifications/models/notification";
import { useSocketStore } from "@/stores/socket-store.ts";
import { merge, Observable } from "rxjs";
import { map, scan } from "rxjs/operators";

export function watchNotifications(): Observable<Notification[]> {
    const client = useSocketStore.getState().client;

    const initial = client
        .watch({ destination: "/app/notifications" })
        .pipe(map((msg) => JSON.parse(msg.body) as Notification[]));

    const updates = client
        .watch({ destination: "/user/queue/notifications" })
        .pipe(map((msg) => JSON.parse(msg.body) as Notification));

    return merge(initial, updates.pipe(map((n) => [n]))).pipe(
        scan((all, next) => [...all, ...next], [] as Notification[]),
    );
}
