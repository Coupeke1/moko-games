import type { Modules } from "@/models/profile/modules";
import type { Statistics } from "@/models/profile/statistics";

export interface Profile {
    id: string;
    username: string;
    email: string;
    description: string;
    image: string;
    statistics: Statistics;
    modules: Modules;
}