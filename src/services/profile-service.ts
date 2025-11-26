import validIdCheck from "@/lib/id";
import axios from 'axios';
import type { KeycloakTokenParsed } from "keycloak-js";
import Keycloak from 'keycloak-js';
import type {Profile} from "@/models/profile.ts";

const BASE_URL = import.meta.env.VITE_USER_SERVICE;

export function addToken(token: string | undefined) {
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    else {
        removeToken()
    }
}

export function removeToken() {
    delete axios.defaults.headers.common['Authorization']
}

export async function findProfile(id: string): Promise<Profile> {
    try {
        validIdCheck(id);
        const { data } = await axios.get<Profile>(`${BASE_URL}/${id}`);

        if (data.id !== id) throw new Error("Profile not found!");
        return data;
    } catch {
        throw new Error(`Profile with id '${id}' could not be fetched`);
    }
}

export async function parseProfile(keycloak: Keycloak, token: string | null): Promise<Profile | null> {
    if (!token) {
        throw new Error("Token not found");
    }

    try {
        addToken(token);

        const parsedToken: KeycloakTokenParsed | undefined = keycloak.tokenParsed;
        if (parsedToken === undefined) throw new Error("Token could not be parsed");

        if (parsedToken.sub === undefined) throw new Error("Id could not be found");

        const id: string = parsedToken.sub;
        const profile = await findProfile(id);

        return profile;
    } catch {
        throw new Error("Profile could not be found");
    }
}