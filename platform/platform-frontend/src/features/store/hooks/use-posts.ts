import { findPost, findPosts } from "@/features/store/services/posts.ts";
import { useQuery } from "@tanstack/react-query";

export function usePosts(id: string | undefined) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["store", id, "posts"],
        queryFn: () => findPosts(id!),
        enabled: !!id,
    });

    return { loading, error, posts: data ?? [] };
}

export function usePost(
    entryId: string | undefined,
    postId: string | undefined,
) {
    const {
        isLoading: loading,
        isError: error,
        data,
    } = useQuery({
        queryKey: ["store", entryId, "posts", postId],
        queryFn: () => findPost(entryId!, postId!),
        enabled: !!entryId && !!postId,
    });

    return { loading, error, post: data ?? null };
}
