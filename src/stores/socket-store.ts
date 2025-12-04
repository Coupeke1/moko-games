import { create } from "zustand";
import type { RxStomp } from "@stomp/rx-stomp";
import { RxStomp as RxStompClass } from "@stomp/rx-stomp";
import { config } from "@/lib/socket-client";
import type { Subscription } from "rxjs";

type SocketState = {
    client: RxStomp;
    isConnected: boolean;
    connecting: boolean;
    error: string | null;
    connections: number;
    connect: () => void;
    disconnect: () => void;
};

const createClient = (): RxStomp => {
    const client = new RxStompClass();
    client.configure(config);
    return client;
};

export const useSocketStore = create<SocketState>((set, get) => {
    const client = createClient();
    let subscription: Subscription | null = null;

    const attachConnectionListener = () => {
        if (subscription) return;

        subscription = client.connectionState$.subscribe({
            next: (state) => {
                set({
                    connecting: state === 0,
                    isConnected: state === 1,
                    error: null,
                });
            },
            error: (err) => {
                set({
                    error: String(err),
                    isConnected: false,
                    connecting: false,
                });
            },
        });
    };

    return {
        client,
        isConnected: false,
        connecting: false,
        error: null,
        connections: 0,
        connect: () => {
            const { connections: connectionCount, isConnected } = get();
            const newCount = connectionCount + 1;
            set({ connections: newCount });

            if (connectionCount === 0 && !isConnected) {
                attachConnectionListener();
                client.activate();
            }
        },
        disconnect: () => {
            const { connections: connectionCount } = get();
            const count = Math.max(0, connectionCount - 1);
            set({ connections: count });

            if (count === 0) {
                subscription?.unsubscribe();
                subscription = null;
                client.deactivate();
                set({ isConnected: false, connecting: false });
            }
        },
    };
});
