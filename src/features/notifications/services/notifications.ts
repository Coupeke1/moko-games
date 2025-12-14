import {client} from "@/lib/api-client.ts";
import {environment} from "@/config.ts";
import type {NotificationItem, NotificationType} from "@/features/notifications/models/notification.ts";

const BASE_URL = environment.communicationService;

export async function getMyNotifications(): Promise<NotificationItem[]> {
    const {data} = await client.get<NotificationItem[]>(`${BASE_URL}/me`);
    return data;
}

export async function getUnreadNotifications(): Promise<NotificationItem[]> {
    const {data} = await client.get<NotificationItem[]>(`${BASE_URL}/unread`);
    return data;
}

export async function getNotificationsByType(
    type: NotificationType,
): Promise<NotificationItem[]> {
    const {data} = await client.get<NotificationItem[]>(`${BASE_URL}/${type}`);
    return data;
}

export async function markNotificationAsRead(id: string): Promise<void> {
    await client.patch(`${BASE_URL}/${id}/read`);
}