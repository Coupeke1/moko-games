import { environment } from "@/config.ts";
import type { Friend } from "@/features/friends/models/friend.ts";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id.ts";

const BASE_URL = environment.socialService;

export async function findFriends() {
    try {
        const { data } = await client.get<Friend[]>(BASE_URL);
        return data;
    } catch {
        throw new Error("Friends could not be fetched");
    }
}

export async function removeFriend(id: string) {
    try {
        validIdCheck(id);
        await client.delete(`${BASE_URL}/remove/${id}`);
    } catch {
        throw new Error("Friend could not be removed");
    }
}
