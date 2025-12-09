import type { Status } from "@/features/friends/models/status.ts";

export interface Friend {
    id: string;
    username: string;
    status: Status;
}