import { byDate } from "@/features/chat/lib/sort";
import type { Type } from "@/features/chat/models/channel/type";
import { findMessages } from "@/features/chat/services/chat";
import { useQuery } from "@tanstack/react-query";

export function useMessages(id: string, type: Type) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["chat", id, "messages"],
        queryFn: async () => {
            const messages = await findMessages(id, type);
            return byDate(messages);
        },
        enabled: !!id,
    });

    return { loading, error, messages: data ?? [] };
}
