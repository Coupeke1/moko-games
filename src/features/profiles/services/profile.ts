import { environment } from "@/config.ts";
import type { Me } from "@/features/profiles/models/me";
import {
    match as modulesEquals,
    type Modules,
} from "@/features/profiles/models/modules.ts";
import {
    match as notificationsMatch,
    type Notifications,
} from "@/features/profiles/models/notifications";
import type { Profile } from "@/features/profiles/models/profile";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id.ts";
import type { KeycloakTokenParsed } from "keycloak-js";
import Keycloak from "keycloak-js";

const BASE_URL = environment.userService;

export async function findMyProfile(id: string): Promise<Me> {
    try {
        validIdCheck(id);
        const { data } = await client.get<Me>(`${BASE_URL}/me`);
        if (data.id !== id) throw new Error("Profile not found!");
        return data;
    } catch {
        throw new Error("Profile could not be fetched");
    }
}

export async function findProfile(id: string): Promise<Profile> {
    try {
        validIdCheck(id);
        const { data } = await client.get<Me>(`${BASE_URL}/${id}`);
        return data;
    } catch {
        throw new Error("Profile could not be fetched");
    }
}

export async function findProfileByName(name: string): Promise<Profile> {
    try {
        const { data } = await client.get<Me>(`${BASE_URL}/find/${name}`);
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
    notifications: Notifications,
) {
    try {
        const profile: Me = await findMyProfile(id);

        await Promise.all([
            updateDescription(profile.description, description),
            updateImage(profile.image, image),
            updateModules(profile.modules, modules),
            updateNotifications(profile.notifications, notifications),
        ]);
    } catch (error) {
        console.error("Profile update failed:", error);
        throw new Error("Profile could not be updated");
    }
}

async function updateDescription(old: string, model: string) {
    if (old === model) return;
    await client.patch(`${BASE_URL}/me/preferences/description`, model, {
        headers: { "Content-Type": "text/plain" },
    });
}

async function updateImage(old: string, model: string) {
    if (old === model) return;
    await client.patch(`${BASE_URL}/me/preferences/image`, model, {
        headers: { "Content-Type": "text/plain" },
    });
}

async function updateModules(old: Modules, model: Modules) {
    if (modulesEquals(old, model)) return;
    await client.patch(`${BASE_URL}/me/preferences/modules`, model);
}

async function updateNotifications(old: Notifications, model: Notifications) {
    if (notificationsMatch(old, model)) return;
    await client.patch(`${BASE_URL}/me/preferences/notifications`, model);
}

export async function parseProfile(
    keycloak: Keycloak,
    token: string | null,
): Promise<Me | null> {
    if (!token) throw new Error("Token not found");

    try {
        const parsedToken: KeycloakTokenParsed | undefined =
            keycloak.tokenParsed;
        if (parsedToken === undefined)
            throw new Error("Token could not be parsed");

        if (parsedToken.sub === undefined)
            throw new Error("Id could not be found");

        const id: string = parsedToken.sub;
        return await findMyProfile(id);
    } catch {
        throw new Error("Profile could not be found");
    }
}
