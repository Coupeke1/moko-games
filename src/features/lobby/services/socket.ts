import type {LobbyMessage} from "@/features/lobby/models/lobby.ts";
import {useSocketStore} from "@/stores/socket-store.ts";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

export function watchLobby(): Observable<LobbyMessage> {
    const client = useSocketStore.getState().client;

    return client
        .watch({destination: "/user/queue/lobby"})
        .pipe(map((msg) => JSON.parse(msg.body) as LobbyMessage));
}