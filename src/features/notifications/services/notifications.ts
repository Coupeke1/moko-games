import { client } from "@/lib/api-client.ts";
import { environment } from "@/config.ts";
import type { Notifications } from "@/features/profile/models/notifications";
import { slug } from "@/lib/format";

const BASE_URL = environment.communicationService;

export async function findMyNotifications(
    type?: string,
    origin?: string,
): Promise<Notifications> {
    try {
        const params = new URLSearchParams();

        if (type && type !== "all") params.append("type", slug(type));
        if (origin && origin !== "all") params.append("origin", slug(origin));

        const url = params.toString()
            ? `${BASE_URL}?${params.toString()}`
            : BASE_URL;

        const { data } = await client.get<Notifications>(url);
        return data;
    } catch {
        throw new Error("Notifications could not be fetched");
    }
}

export async function readNotification(id: string): Promise<void> {
    try {
        await client.patch(`${BASE_URL}/${id}/read`);
    } catch {
        throw new Error("Notification could not be read");
    }
}
