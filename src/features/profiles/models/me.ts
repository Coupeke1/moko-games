import type { Modules } from "@/features/profiles/models/modules.ts";
import type { Notifications } from "@/features/profiles/models/notifications";
import type { Statistics } from "@/features/profiles/models/statistics.ts";

export interface Me {
    id: string;
    username: string;
    email: string;
    description: string;
    image: string;
    statistics: Statistics;
    modules: Modules;
    notifications: Notifications;
}
