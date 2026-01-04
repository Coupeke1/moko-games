import { environment } from "@/config.ts";
import type { Channel } from "@/features/chat/models/channel/channel";
import type {
    BotReference,
    LobbyReference,
    PrivateReference,
} from "@/features/chat/models/channel/reference";
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

export async function sendMessage(id: string, type: Type, message: string) {
    try {
        validIdCheck(id);
        await client.post(`${BASE_URL}/${type.toLowerCase()}/${id}`, {
            content: message,
        });
    } catch {
        throw new Error("Message could not be sent");
    }
}

export function getSender(channel: Channel, message: Message): Bot | User {
    switch (channel.type) {
        case Type.Bot:
            return getBotSender(channel, message);
        case Type.Lobby:
            return getLobbySender(channel, message);
        case Type.Friends:
            return getPrivateSender(channel, message);
        default:
            throw new Error("Could not get sender");
    }
}

function getBotSender(channel: Channel, message: Message): User {
    const reference: BotReference = channel.referenceType as BotReference;

    if (message.senderId === reference.user.id) return reference.user;
    else if (message.senderId === reference.bot.id) return reference.bot;

    throw new Error("Could not get sender");
}

function getLobbySender(channel: Channel, message: Message): User {
    const reference: LobbyReference = channel.referenceType as LobbyReference;

    const player = reference.players.find(
        (player) => player.id === message.senderId,
    );

    if (player === undefined) throw new Error("Could not get sender");
    return player;
}

function getPrivateSender(channel: Channel, message: Message): User {
    const reference: PrivateReference =
        channel.referenceType as PrivateReference;

    if (message.senderId === reference.user.id) return reference.user;
    else if (message.senderId === reference.otherUser.id)
        return reference.otherUser;

    throw new Error("Could not get sender");
}
