import { findPrivateChannel } from "@/features/chat/services/channel";
import { useQuery } from "@tanstack/react-query";

export function useChannel(id: string) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["channels", id],
        queryFn: () => findPrivateChannel(id),
    });

    return { loading, error, channel: data ?? null };
}
