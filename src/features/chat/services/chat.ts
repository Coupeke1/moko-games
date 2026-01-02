import { environment } from "@/config.ts";
import type { Message } from "@/features/chat/models/message";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id";

const BASE_URL = environment.chatService;

export async function findMessages(id: string): Promise<Message[]> {
    try {
        validIdCheck(id);
        const { data } = await client.get<Message[]>(
            `${BASE_URL}/channel/friends/${id}`,
        );
        return data;
    } catch {
        throw new Error("Channel could not be fetched");
    }
}

export async function sendPrivateMessage(id: string, message: string) {
    try {
        validIdCheck(id);
        await client.post(`${BASE_URL}/friends/${id}`, {
            content: message,
        });
    } catch {
        throw new Error("Message could not be sent");
    }
}
