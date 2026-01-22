import type { Friend } from "@/features/friends/models/friend.ts";

export interface Overview {
    friends: Friend[];
    incoming: Friend[];
    outgoing: Friend[];
}