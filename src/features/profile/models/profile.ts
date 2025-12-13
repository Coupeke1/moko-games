import type { Modules } from "@/features/profile/models/modules.ts";
import type { Notifications } from "@/features/profile/models/notifications";
import type { Statistics } from "@/features/profile/models/statistics.ts";

export interface Profile {
    id: string;
    username: string;
    email: string;
    description: string;
    image: string;
    statistics: Statistics;
    modules: Modules;
    notifications: Notifications;
}
