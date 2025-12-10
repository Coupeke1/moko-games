import { useEffect } from "react";
import { useAuthStore } from "@/stores/auth-store";
import { environment } from "@/config";

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
