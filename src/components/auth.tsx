import {useAuthStore} from "@/stores/auth-store.tsx";
import {useEffect} from "react";
import LoadingState from "@/components/state/loading.tsx";
import {environment} from "@/config.ts";

const config: Keycloak.KeycloakConfig = {
    url: environment.authUrl,
    realm: environment.authRealm,
    clientId: environment.authClientId
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