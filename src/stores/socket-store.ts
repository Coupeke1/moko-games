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

    connect: () => void;
    disconnect: () => Promise<void>;
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
            error: (err) => set({ error: String(err) }),
        });
    };

    return {
        client,
        isConnected: false,
        connecting: false,
        error: null,

        connect: () => {
            const { isConnected } = get();
            if (isConnected) return;

            attachConnectionListener();
            client.activate();
        },

        disconnect: async () => {
            subscription?.unsubscribe();
            subscription = null;
            await client.deactivate();
            set({ isConnected: false, connecting: false });
        },
    };
});
