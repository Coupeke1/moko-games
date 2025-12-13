import { client } from "@/lib/api-client.ts";
import { environment } from "@/config.ts";
import { validIdCheck } from "@/lib/id.ts";
import {
    match as modulesEquals,
    type Modules,
} from "@/features/profile/models/modules.ts";
import type { Profile } from "@/features/profile/models/profile.ts";
import type { KeycloakTokenParsed } from "keycloak-js";
import Keycloak from "keycloak-js";
import {
    match as notificationsMatch,
    type Notifications,
} from "@/features/profile/models/notifications";

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
    notifications: Notifications,
) {
    try {
        const profile: Profile = await findProfile(id);

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
