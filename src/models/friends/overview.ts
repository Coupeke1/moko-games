import type { Friend } from "@/models/friends/friend";

export interface Overview {
    friends: Friend[];
    incoming: Friend[];
    outgoing: Friend[];
}