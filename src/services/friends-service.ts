import type { Friend } from "@/models/friends/friend";
import type { Overview } from "@/models/friends/overview";
import type { Profile } from "@/models/profile/profile";
import axios from "axios";

const PROFILE_URL = import.meta.env.VITE_USER_SERVICE;
const SOCIAL_URL = import.meta.env.VITE_SOCIAL_SERVICE;

export async function findFriends() {
    try {
        const { data: friendsResponse } = await axios.get<Overview>(SOCIAL_URL);
        if (!friendsResponse?.friends) return [];

        const details = await Promise.all(
            friendsResponse.friends.map(async (friend: Friend) => {
                try {
                    const { data: friendResponse } = await axios.get<Profile>(`${PROFILE_URL}/${friend.id}`);
                    return friendResponse;
                }
                catch {
                    throw new Error(`Friend with id '${friend.id}' could not be fetched`);
                }
            })
        );

        return details;
    } catch {
        throw new Error("Friends could not be fetched");
    }
}