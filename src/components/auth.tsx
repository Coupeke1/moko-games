import Page from "@/components/layout/page";
import LoadingState from "@/components/state/loading";
import { useAuthStore } from "@/stores/auth-store";
import { useEffect } from "react";

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
        <Page>
            <LoadingState />
        </Page>
    )
    return <></>;
}