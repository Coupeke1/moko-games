
import { validIdCheck } from "@/lib/id";
import type { Profile } from "@/models/profile";
import axios from 'axios';
import type { KeycloakTokenParsed } from "keycloak-js";
import Keycloak from 'keycloak-js';
import { redirect, useNavigate } from "react-router";

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
        const { data } = await axios.get<Profile>(`${BASE_URL}/me`);

        if (data.id !== id) throw new Error("Profile not found!");
        return data;
    } catch {
        throw new Error(`Profile with id '${id}' could not be fetched`);
    }
}

export async function updateProfile(id: string, description: string, image: string) {
    try {
        const profile: Profile = await findProfile(id);
        await updateDescription(profile.description, description);
        await updateImage(profile.image, image);
    } catch {
        throw new Error(`Profile with id '${id}' could not be updated`);
    }
}

async function updateDescription(old: string, model: string) {
    if (old === model) return;
    await axios.patch(`${BASE_URL}/me/description`, model, {
        headers: { 'Content-Type': 'text/plain' }
    });
}

async function updateImage(old: string, model: string) {
    if (old === model) return;
    await axios.patch(`${BASE_URL}/me/image`, model, {
        headers: { 'Content-Type': 'text/plain' }
    });
}

async function updateModules() {
    
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