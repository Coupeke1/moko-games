import type { Lobby } from "@/models/lobby/lobby";
import { useSocketStore } from "@/stores/socket-store";
import type { IMessage } from "@stomp/rx-stomp";
import type { Observable } from "rxjs";
import { map } from "rxjs/operators";

export function watchLobby(id: string): Observable<Lobby> {
    const client = useSocketStore.getState().client;
    const isConnected = useSocketStore.getState().isConnected;

    if (!isConnected) useSocketStore.getState().connect();

    const destination = `/app/lobbies/${id}`;

    return client
        .watch({ destination })
        .pipe(map((msg: IMessage) => JSON.parse(msg.body) as Lobby));
}
