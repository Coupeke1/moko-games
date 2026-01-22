import type {Lobby, LobbyMessage} from "@/features/lobby/models/lobby.ts";
import {useSocketStore} from "@/stores/socket-store.ts";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

export function watchLobby(): Observable<LobbyMessage> {
    const client = useSocketStore.getState().client;

    return client
        .watch({destination: "/user/queue/lobby"})
        .pipe(
            map((msg) => {
                const parsed = JSON.parse(msg.body);

                if ("payload" in parsed || "reason" in parsed) {
                    return parsed as LobbyMessage;
                }

                return {
                    userId: "",
                    queue: "lobby",
                    payload: parsed as Lobby,
                    reason: null,
                } as LobbyMessage;
            }),
        );
}