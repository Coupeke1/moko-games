import { environment } from "@/config";
import { client } from "@/lib/api-client";
import type { Entries } from "@/models/store/entries";
import type { Entry } from "@/models/store/entry";
import type { Post } from "@/models/store/post/post";

const BASE_URL = environment.storeService;

export async function findEntries(
    query?: string,
    sorting?: string,
    category?: string,
): Promise<Entries> {
    try {
        const params = new URLSearchParams();

        if (query) params.append("query", query);
        if (sorting) params.append("sorting", sorting);
        if (category && category !== "all")
            params.append("category", category.toUpperCase());

        const url = params.toString()
            ? `${BASE_URL}?${params.toString()}`
            : BASE_URL;

        const { data } = await client.get<Entries>(url);
        return data;
    } catch {
        throw new Error("Catalog could not be fetched");
    }
}

export async function findEntry(id: string): Promise<Entry> {
    try {
        const { data } = await client.get<Entry>(`${BASE_URL}/${id}`);
        return data;
    } catch {
        throw new Error(`Entry with id '${id}' could not be fetched`);
    }
}

export async function findPosts(id: string): Promise<Post[]> {
    try {
        const { data } = await client.get<Post[]>(`${BASE_URL}/${id}/posts`);
        return data;
    } catch {
        throw new Error(`Posts for entry with id '${id}' could not be fetched`);
    }
}

export async function findPost(entryId: string, postId: string): Promise<Post> {
    try {
        const { data } = await client.get<Post>(
            `${BASE_URL}/${entryId}/posts/${postId}`,
        );
        return data;
    } catch {
        throw new Error(
            `Post with id '${postId}' for entry with id '${entryId}' could not be fetched`,
        );
    }
}
