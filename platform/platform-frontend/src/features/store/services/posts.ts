import { environment } from "@/config.ts";
import { client } from "@/lib/api-client.ts";
import type { Post } from "@/features/store/models/post/post.ts";

const BASE_URL = environment.storeService;

export async function findPosts(id: string): Promise<Post[]> {
    try {
        const { data } = await client.get<Post[]>(`${BASE_URL}/${id}/posts`);
        return data;
    } catch {
        throw new Error("Posts could not be fetched");
    }
}

export async function findPost(entryId: string, postId: string): Promise<Post> {
    try {
        const { data } = await client.get<Post>(
            `${BASE_URL}/${entryId}/posts/${postId}`,
        );
        return data;
    } catch {
        throw new Error("Post could not be fetched");
    }
}
