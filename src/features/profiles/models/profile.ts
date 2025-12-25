import type { Achievement } from "@/features/profiles/models/achievement";
import type { Favourite } from "@/features/profiles/models/favourite";
import type { Statistics } from "@/features/profiles/models/statistics.ts";

export interface Profile {
    id: string;
    username: string;
    email: string;
    description: string;
    image: string;
    statistics: Statistics;
    achievements: Achievement[] | null;
    favourites: Favourite[] | null;
}
