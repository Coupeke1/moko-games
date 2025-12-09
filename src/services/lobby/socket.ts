import type { Lobby } from "@/models/lobby/lobby";
import { useSocketStore } from "@/stores/socket-store";
import type { IMessage } from "@stomp/rx-stomp";
import { Observable, merge } from "rxjs";
import { map, take } from "rxjs/operators";

export function watchLobby(id: string): Observable<Lobby> {
    const client = useSocketStore.getState().client;

    const initial = client.watch({ destination: `/app/lobbies/${id}` }).pipe(
        take(1),
        map((msg: IMessage) => JSON.parse(msg.body) as Lobby),
    );

    const updates = client
        .watch({ destination: "/user/queue/lobby" })
        .pipe(map((msg: IMessage) => JSON.parse(msg.body) as Lobby));

    return merge(initial, updates);
}
