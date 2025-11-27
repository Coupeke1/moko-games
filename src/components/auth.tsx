import Page from "@/components/layout/page";
import LoadingState from "@/components/state/loading";
import { config } from "@/config";
import { useAuthStore } from "@/stores/auth-store";
import { useEffect } from "react";

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
        <Page>
            <LoadingState />
        </Page>
    )
    return <></>;
}