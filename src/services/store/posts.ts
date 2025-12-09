import { environment } from "@/config";
import { client } from "@/lib/api-client";
import type { Post } from "@/models/store/post/post";

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
