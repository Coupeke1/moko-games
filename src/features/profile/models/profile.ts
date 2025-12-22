import type { Statistics } from "@/features/profile/models/statistics.ts";

export interface Profile {
    id: string;
    username: string;
    description: string;
    image: string;
    statistics: Statistics;
}
