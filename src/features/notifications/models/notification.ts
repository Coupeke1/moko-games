import type { Origin } from "@/features/notifications/models/type";

export interface Notification {
    id: string;
    origin: Origin;
    title: string;
    message: string;
    createdAt: string;
    read: boolean;
}
