import Page from "@/components/layout/page";
import LoadingState from "@/components/state/loading";
import { useProfile } from "@/hooks/use-profile";
import { useAuthStore } from "@/stores/auth-store";
import { useEffect } from "react";
import ErrorState from "@/components/state/error";
import { config } from "@/config";

export default function Auth() {
    const initAuth = useAuthStore((state) => state.init);
    const initialized = useAuthStore((state) => state.initialized);
    const { isLoading, isError } = useProfile();

    useEffect(() => {
        initAuth({
            url: config.authUrl,
            realm: config.authRealm,
            clientId: config.authClientId,
        } as Keycloak.KeycloakConfig);
    }, []);

    if (!initialized || isLoading)
        return (
            <Page>
                <LoadingState />
            </Page>
        );

    if (isError)
        return (
            <Page>
                <ErrorState />
            </Page>
        );

    return <></>;
}
