import Page from "@/components/layout/page";
import LoadingState from "@/components/state/loading";
import {useProfile} from "@/hooks/use-profile.ts";
import {useAuthStore} from "@/stores/auth-store";
import {useEffect, useRef} from "react";
import ErrorState from "@/components/state/error";
import {environment} from "@/config";

export default function Auth() {
    const initAuth = useAuthStore((state) => state.init);
    const initialized = useAuthStore((state) => state.initialized);
    const authenticated = useAuthStore((state) => state.authenticated);
    const {loading: isLoading, error: isError} = useProfile();

    const hasInitialized = useRef(false);

    useEffect(() => {
        if (!hasInitialized.current) {
            hasInitialized.current = true;
            initAuth({
                url: environment.authUrl,
                realm: environment.authRealm,
                clientId: environment.authClientId,
            });
        }
    }, [initAuth]);

    if (!initialized || (authenticated && isLoading))
        return (
            <Page>
                <LoadingState/>
            </Page>
        );

    if (isError)
        return (
            <Page>
                <ErrorState/>
            </Page>
        );

    return <></>;
}