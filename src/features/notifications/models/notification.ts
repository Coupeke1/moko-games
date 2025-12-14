import type { Origin } from "@/features/notifications/models/type";

export interface Notification {
    id: string;
    type: Origin;
    title: string;
    message: string;
    createdAt: string;
    read: boolean;
}

export type ReadFilter = "all" | "unread" | "read";
