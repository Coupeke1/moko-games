import { environment } from "@/config";
import { client } from "@/lib/api-client";
import { validIdCheck } from "@/lib/id";
import type { Friend } from "@/models/friends/friend";
import type { Profile } from "@/models/profile/profile";

const PROFILE_URL = environment.userService;
const SOCIAL_URL = environment.socialService;

export async function findFriends() {
    try {
        const { data: response } = await client.get<Friend[]>(SOCIAL_URL);

        const details = await Promise.all(
            response.map(async (friend: Friend) => {
                try {
                    const { data } = await client.get<Profile>(
                        `${PROFILE_URL}/${friend.id}`,
                    );
                    return data;
                } catch {
                    throw new Error(
                        `Friend with id '${friend.id}' could not be fetched`,
                    );
                }
            }),
        );

        return details;
    } catch {
        throw new Error("Friends could not be fetched");
    }
}

export async function findIncomingRequests() {
    try {
        const { data: response } = await client.get<Friend[]>(
            `${SOCIAL_URL}/requests/incoming`,
        );

        const details = await Promise.all(
            response.map(async (friend: Friend) => {
                try {
                    const { data } = await client.get<Profile>(
                        `${PROFILE_URL}/${friend.id}`,
                    );
                    return data;
                } catch {
                    throw new Error(
                        `Friend with id '${friend.id}' could not be fetched`,
                    );
                }
            }),
        );

        return details;
    } catch {
        throw new Error("Incoming requests could not be fetched");
    }
}

export async function findOutgoingRequests() {
    try {
        const { data: response } = await client.get<Friend[]>(
            `${SOCIAL_URL}/requests/outgoing`,
        );

        const details = await Promise.all(
            response.map(async (friend: Friend) => {
                try {
                    const { data } = await client.get<Profile>(
                        `${PROFILE_URL}/${friend.id}`,
                    );
                    return data;
                } catch {
                    throw new Error(
                        `Friend with id '${friend.id}' could not be fetched`,
                    );
                }
            }),
        );

        return details;
    } catch {
        throw new Error("Outgoing requests could not be fetched");
    }
}

export async function sendRequest(username: string) {
    if (username === null || username.length <= 0) {
        throw new Error("Username is not valid");
    }

    try {
        await client.post(SOCIAL_URL, { username });
    } catch {
        throw new Error(`Request to '${username}' could not be sent`);
    }
}

export async function removeFriend(id: string) {
    try {
        validIdCheck(id);
        await client.delete(`${SOCIAL_URL}/remove/${id}`);
    } catch {
        throw new Error(`Friend with id '${id}' could not be removed`);
    }
}

export async function acceptRequest(id: string) {
    try {
        validIdCheck(id);
        await client.post(`${SOCIAL_URL}/accept/${id}`);
    } catch {
        throw new Error(
            `Request from user with id '${id}' could not be accepted`,
        );
    }
}

export async function rejectRequest(id: string) {
    try {
        validIdCheck(id);
        await client.post(`${SOCIAL_URL}/reject/${id}`);
    } catch {
        throw new Error(
            `Request from user with id '${id}' could not be rejected`,
        );
    }
}

export async function cancelRequest(id: string) {
    try {
        validIdCheck(id);
        await client.post(`${SOCIAL_URL}/cancel/${id}`);
    } catch {
        throw new Error(
            `Request from user with id '${id}' could not be cancelled`,
        );
    }
}
