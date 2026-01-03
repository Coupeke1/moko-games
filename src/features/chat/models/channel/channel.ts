import type {
    BotReference,
    LobbyReference,
    PrivateReference,
} from "@/features/chat/models/channel/reference";
import type { Type } from "@/features/chat/models/channel/type";

export interface Channel {
    id: string;
    type: Type;
    referenceType: BotReference | LobbyReference | PrivateReference;
}
