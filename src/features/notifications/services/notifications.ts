import { environment } from "@/config.ts";
import type { Notification } from "@/features/notifications/models/notification";
import { Origin } from "@/features/notifications/models/origin";
import { client } from "@/lib/api-client.ts";
import { slug } from "@/lib/format";
import type { PagedResponse } from "@/types/paged-response";

const BASE_URL = environment.communicationService;

export async function findMyNotifications(
    type?: string,
    origin?: string,
    page?: number,
): Promise<PagedResponse<Notification>> {
    try {
        const params = new URLSearchParams();

        params.append("page", (page ?? 0).toString());

        if (type && type !== "all") params.append("type", slug(type));
        if (origin && origin !== "all") params.append("origin", slug(origin));

        const { data } = await client.get<PagedResponse<Notification>>(
            `${BASE_URL}?${params.toString()}`,
        );
        return data;
    } catch {
        throw new Error("Notifications could not be fetched");
    }
}

export async function findMyNotificationsSince(
    since: string,
): Promise<Notification[]> {
    const params = new URLSearchParams();
    params.append("time", since);

    const { data } = await client.get<Notification[]>(
        `${BASE_URL}/since?${params.toString()}`,
    );

    return data;
}

export async function readNotification(id: string): Promise<void> {
    try {
        await client.patch(`${BASE_URL}/${id}/read`);
    } catch {
        throw new Error("Notification could not be read");
    }
}

export function getRoute(notification: Notification): string | null {
    switch (notification.origin) {
        case Origin.Achievement:
            return "/profile";
        case Origin.FriendAccepted:
            return "/friends";
        case Origin.FriendReceived:
            return "/friends/requests";
        case Origin.LobbyInvite:
            return "/library";
        case Origin.Message:
            return "/chat";
        case Origin.Order:
            return "/library";
    }

    return null;
}
