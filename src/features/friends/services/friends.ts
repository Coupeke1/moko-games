import { environment } from "@/config.ts";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id.ts";
import type { Friend } from "@/features/friends/models/friend.ts";
import type { Profile } from "@/features/profile/models/profile.ts";

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
                    throw new Error("Friend could not be fetched");
                }
            }),
        );

        return details;
    } catch {
        throw new Error("Friends could not be fetched");
    }
}

export async function removeFriend(id: string) {
    try {
        validIdCheck(id);
        await client.delete(`${SOCIAL_URL}/remove/${id}`);
    } catch {
        throw new Error("Friend could not be removed");
    }
}