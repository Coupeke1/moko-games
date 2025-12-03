import { RxStomp } from "@stomp/rx-stomp";
import { RxStompConfig } from "@stomp/rx-stomp";
import SockJS from "sockjs-client";
import { environment } from "@/config.ts";
import { useAuthStore } from "@/stores/auth-store";

const BASE_URL = environment.sessionSocket;

function getHeaders() {
    const token = useAuthStore.getState().token;
    return token ? { Authorization: `Bearer ${token}` } : { Authorization: "" };
}

export const config: RxStompConfig = {
    webSocketFactory: () => new SockJS(`${BASE_URL}/ws`),

    connectHeaders: {},
    beforeConnect: (client: RxStomp) => {
        const headers = getHeaders();
        client.configure({ connectHeaders: headers });
    },

    heartbeatIncoming: 0,
    heartbeatOutgoing: 20000,
    reconnectDelay: 5000,

    debug: (msg: string): void => {
        console.log("DEBUG: " + msg);
    },
};

let instance: RxStomp | null = null;

export function getClient(): RxStomp {
    if (!instance) {
        const client = new RxStomp();

        client.configure(config);
        instance = client;
    }

    return instance;
}
