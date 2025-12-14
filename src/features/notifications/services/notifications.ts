import { client } from "@/lib/api-client.ts";
import { environment } from "@/config.ts";
import type {
    Notification,
    NotificationType,
} from "@/features/notifications/models/notification.ts";

const BASE_URL = environment.communicationService;

export async function getMyNotifications(): Promise<Notification[]> {
    const { data } = await client.get<Notification[]>(`${BASE_URL}/me`);
    return data;
}

export async function getUnreadNotifications(): Promise<Notification[]> {
    const { data } = await client.get<Notification[]>(`${BASE_URL}/unread`);
    return data;
}

export async function getNotificationsByType(
    type: NotificationType,
): Promise<Notification[]> {
    const { data } = await client.get<Notification[]>(`${BASE_URL}/${type}`);
    return data;
}

export async function markNotificationAsRead(id: string): Promise<void> {
    await client.patch(`${BASE_URL}/${id}/read`);
}
