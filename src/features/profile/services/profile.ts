import { client } from "@/lib/api-client.ts";

import { environment } from "@/config.ts";
import { validIdCheck } from "@/lib/id.ts";
import type { Modules } from "@/features/profile/models/modules.ts";
import type { Profile } from "@/features/profile/models/profile.ts";
import type { KeycloakTokenParsed } from "keycloak-js";
import Keycloak from "keycloak-js";

const BASE_URL = environment.userService;

export async function findProfile(id: string): Promise<Profile> {
    try {
        validIdCheck(id);
        const { data } = await client.get<Profile>(`${BASE_URL}/me`);

        if (data.id !== id) throw new Error("Profile not found!");
        return data;
    } catch {
        throw new Error("Profile could not be fetched");
    }
}

export async function updateProfile(
    id: string,
    description: string,
    image: string,
    modules: Modules,
) {
    try {
        const profile: Profile = await findProfile(id);
        await updateDescription(profile.description, description);
        await updateImage(profile.image, image);
        await updateModules(profile.modules, modules);
    } catch {
        throw new Error("Profile could not be updated");
    }
}

async function updateDescription(old: string, model: string) {
    if (old === model) return;
    await client.patch(`${BASE_URL}/me/description`, model, {
        headers: { "Content-Type": "text/plain" },
    });
}

async function updateImage(old: string, model: string) {
    if (old === model) return;
    await client.patch(`${BASE_URL}/me/image`, model, {
        headers: { "Content-Type": "text/plain" },
    });
}

async function updateModules(old: Modules, model: Modules) {
    if (
        old.achievements === model.achievements &&
        old.favourites === model.favourites
    )
        return;
    await client.patch(`${BASE_URL}/me/modules`, model);
}

export async function parseProfile(
    keycloak: Keycloak,
    token: string | null,
): Promise<Profile | null> {
    if (!token) {
        throw new Error("Token not found");
    }

    try {
        const parsedToken: KeycloakTokenParsed | undefined =
            keycloak.tokenParsed;
        if (parsedToken === undefined)
            throw new Error("Token could not be parsed");

        if (parsedToken.sub === undefined)
            throw new Error("Id could not be found");

        const id: string = parsedToken.sub;
        return await findProfile(id);
    } catch {
        throw new Error("Profile could not be found");
    }
}
