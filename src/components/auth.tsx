import Page from "@/components/layout/page";
import LoadingState from "@/components/state/loading";
import { useProfile } from "@/hooks/use-profile";
import { useAuthStore } from "@/stores/auth-store";
import { useEffect } from "react";
import ErrorState from "./state/error";

const config: Keycloak.KeycloakConfig = {
    url: import.meta.env.VITE_AUTH_URL,
    realm: import.meta.env.VITE_AUTH_REALM,
    clientId: import.meta.env.VITE_AUTH_CLIENT
};

export default function Auth() {
    const initAuth = useAuthStore((state) => state.init);
    const initialized = useAuthStore((state) => state.initialized);
    const { isLoading, isError } = useProfile();

    useEffect(() => {
        initAuth(config);
    }, []);

    if (!initialized || isLoading) return (
        <Page>
            <LoadingState />
        </Page>
    );

    if (isError) return (
        <Page>
            <ErrorState />
        </Page>
    )

    return <></>;
}