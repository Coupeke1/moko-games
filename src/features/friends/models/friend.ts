import type { Statistics } from "@/features/friends/models/statistics";
import type { Status } from "@/features/friends/models/status.ts";

export interface Friend {
    id: string;
    username: string;
    image: string;
    statistics: Statistics;
    status: Status;
}
