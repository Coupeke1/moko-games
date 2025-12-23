import { environment } from "@/config.ts";
import type { Friend } from "@/features/friends/models/friend.ts";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id.ts";

const BASE_URL = environment.socialService;

export async function findIncomingRequests() {
    try {
        const { data } = await client.get<Friend[]>(
            `${BASE_URL}/requests/incoming`,
        );
        return data;
    } catch {
        throw new Error("Incoming requests could not be fetched");
    }
}

export async function sendRequest(username: string) {
    if (username === null || username.length <= 0) {
        throw new Error("Username is not valid");
    }

    try {
        await client.post(BASE_URL, { username });
    } catch {
        throw new Error("Request could not be sent");
    }
}

export async function acceptRequest(id: string) {
    try {
        validIdCheck(id);
        await client.post(`${BASE_URL}/accept/${id}`);
    } catch {
        throw new Error("Request could not be accepted");
    }
}

export async function rejectRequest(id: string) {
    try {
        validIdCheck(id);
        await client.post(`${BASE_URL}/reject/${id}`);
    } catch {
        throw new Error("Request could not be rejected");
    }
}

export async function cancelRequest(id: string) {
    try {
        validIdCheck(id);
        await client.post(`${BASE_URL}/cancel/${id}`);
    } catch {
        throw new Error("Request could not be cancelled");
    }
}
