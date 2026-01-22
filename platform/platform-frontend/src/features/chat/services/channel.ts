import { environment } from "@/config.ts";
import type { Channel } from "@/features/chat/models/channel/channel";
import type { Type } from "@/features/chat/models/channel/type";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id";

const BASE_URL = environment.chatService;

export async function findChannel(id: string, type: Type): Promise<Channel> {
    try {
        validIdCheck(id);
        const { data } = await client.get<Channel>(
            `${BASE_URL}/channel/${type.toLowerCase()}/${id}`,
        );
        return data;
    } catch {
        throw new Error("Channel could not be fetched");
    }
}
