import { environment } from "@/config";
import { useAuthStore } from "@/stores/auth-store";
import { useEffect } from "react";

export default function Auth() {
    const initAuth = useAuthStore((state) => state.init);

    useEffect(() => {
        initAuth({
            url: environment.authUrl,
            realm: environment.authRealm,
            clientId: environment.authClientId,
        } as Keycloak.KeycloakConfig);
    }, []);

    return null;
}
