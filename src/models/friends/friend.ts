import type { Status } from "@/models/friends/status";

export interface Friend {
    id: string;
    username: string;
    status: Status;
}