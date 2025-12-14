import type { Notification } from "@/features/notifications/models/notification.ts";

export interface Notifications {
    items: Notification[];
    page: number;
    size: number;
    last: boolean;
}
