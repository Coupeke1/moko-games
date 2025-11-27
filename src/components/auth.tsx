import {useAuthStore} from "@/stores/auth-store.tsx";
import {useEffect} from "react";
import LoadingState from "@/components/state/loading.tsx";
import {config} from "@/config.ts";

const configIdp: Keycloak.KeycloakConfig = {
    url: config.authUrl,
    realm: config.authRealm,
    clientId: config.authClientId
};

export default function Auth() {
    const initAuth = useAuthStore((state) => state.init);
    const initialized = useAuthStore((state) => state.initialized);

    useEffect(() => {
        initAuth(configIdp);
    }, []);

    if (!initialized) return (
        <LoadingState />
    )
    return <></>;
}