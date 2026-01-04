import validIdCheck from "@/lib/id";
import axios from 'axios';
import type {KeycloakTokenParsed} from "keycloak-js";
import Keycloak from 'keycloak-js';
import type {Profile} from "@/models/profile.ts";
import {environment} from "@/config.ts";

const BASE_URL = environment.userService;

export function addToken(token: string | undefined) {
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    else {
        removeToken()
    }
}

export function removeToken() {
    delete axios.defaults.headers.common['Authorization']
}

export async function getMyProfile(): Promise<Profile> {
    try {
        const { data } = await axios.get<Profile>(`${BASE_URL}/me`);
        return data;
    } catch {
        throw new Error("Could not fetch my profile");
    }
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

        return await getMyProfile();
    } catch {
        const parsedToken: KeycloakTokenParsed | undefined = keycloak.tokenParsed;
        if (parsedToken?.sub) {
            try {
                return await findProfile(parsedToken.sub);
            } catch {
                // ignore
            }
        }
        throw new Error("Profile could not be found");
    }
}