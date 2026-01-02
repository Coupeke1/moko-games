import { environment } from "@/config.ts";
import type { Channel } from "@/features/chat/models/channel";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id";

const BASE_URL = environment.chatService;

export async function findPrivateChannel(id: string): Promise<Channel> {
    try {
        validIdCheck(id);
        const { data } = await client.get<Channel>(
            `${BASE_URL}/channel/friends/${id}`,
        );
        return data;
    } catch {
        throw new Error("Channel could not be fetched");
    }
}
