import type { Friend } from "@/models/friends/friend";
import type { Overview } from "@/models/friends/overview";
import type { Profile } from "@/models/profile/profile";
import axios from "axios";

const PROFILE_URL = import.meta.env.VITE_USER_SERVICE;
const SOCIAL_URL = import.meta.env.VITE_SOCIAL_SERVICE;

export async function findFriends() {
    try {
        const { data: response } = await axios.get<Overview>(SOCIAL_URL);
        if (!response?.friends) return [];

        const details = await Promise.all(
            response.friends.map(async (friend: Friend) => {
                try {
                    const { data } = await axios.get<Profile>(`${PROFILE_URL}/${friend.id}`);
                    return data;
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

export async function findIncomingRequests() {
    try {
        const { data } = await axios.get<Friend[]>(`${SOCIAL_URL}/requests/incoming`);
        return data;
    } catch {
        throw new Error("Incoming requests could not be fetched");
    }
}

export async function findOutgoingRequests() {
    try {
        const { data } = await axios.get<Friend[]>(`${SOCIAL_URL}/requests/outgoing`);
        return data;
    } catch {
        throw new Error("Outgoing requests could not be fetched");
    }
}

export async function sendRequest(username: string) {
    if (username === null || username.length <= 0) {
        throw new Error("Username is not valid");
    }

    try {
        await axios.post(SOCIAL_URL, { username });
    } catch {
        throw new Error(`Request to '${username}' could not be sent`);
    }
}