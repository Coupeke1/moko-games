import {useAuthStore} from "@/stores/auth-store.tsx";
import {useEffect} from "react";
import LoadingState from "@/components/state/loading.tsx";

const config: Keycloak.KeycloakConfig = {
    url: import.meta.env.VITE_AUTH_URL,
    realm: import.meta.env.VITE_AUTH_REALM,
    clientId: import.meta.env.VITE_AUTH_CLIENT
};

export default function Auth() {
    const initAuth = useAuthStore((state) => state.init);
    const initialized = useAuthStore((state) => state.initialized);

    useEffect(() => {
        initAuth(config);
    }, []);

    if (!initialized) return (
        <LoadingState />
    )
    return <></>;
}