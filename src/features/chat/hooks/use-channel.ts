import type { Type } from "@/features/chat/models/channel/type";
import { findChannel } from "@/features/chat/services/channel";
import { useQuery } from "@tanstack/react-query";

export function useChannel(id: string | null, type: Type) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["channels", id],
        queryFn: () => findChannel(id!, type),
        enabled: !!id,
    });

    return { loading, error, channel: data ?? null };
}
