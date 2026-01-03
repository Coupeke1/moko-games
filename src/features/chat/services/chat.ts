import { environment } from "@/config.ts";
import type { Channel } from "@/features/chat/models/channel/channel";
import type { PrivateReference } from "@/features/chat/models/channel/reference";
import { Type } from "@/features/chat/models/channel/type";
import type { User } from "@/features/chat/models/channel/user";
import type { Message } from "@/features/chat/models/message";
import type { Bot } from "@/features/profiles/models/bot";
import { client } from "@/lib/api-client.ts";
import { validIdCheck } from "@/lib/id";

const BASE_URL = environment.chatService;

export async function findMessages(id: string, type: Type): Promise<Message[]> {
    try {
        validIdCheck(id);
        const { data } = await client.get<Message[]>(
            `${BASE_URL}/${type.toLowerCase()}/${id}`,
        );
        return data;
    } catch {
        throw new Error("Messages could not be fetched");
    }
}

export async function sendPrivateMessage(id: string, message: string) {
    try {
        validIdCheck(id);
        await client.post(`${BASE_URL}/friends/${id}`, { content: message });
    } catch {
        throw new Error("Message could not be sent");
    }
}

export function getSender(channel: Channel, message: Message): Bot | User {
    switch (channel.type) {
        // case Type.Bot:
        //     return {};
        // case Type.Lobby:
        //     return {};
        case Type.Friends:
            return getPrivateSender(channel, message);
        default:
            throw new Error("Could not get sender");
    }
}

function getPrivateSender(channel: Channel, message: Message): User {
    const reference: PrivateReference =
        channel.referenceType as PrivateReference;

    if (message.senderId === reference.user.id) return reference.user;
    else if (message.senderId === reference.otherUser.id)
        return reference.otherUser;

    throw new Error("Could not get sender");
}
