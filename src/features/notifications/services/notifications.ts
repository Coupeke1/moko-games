import { environment } from "@/config.ts";
import { client } from "@/lib/api-client.ts";
import { slug } from "@/lib/format";
import type { PagedResponse } from "@/types/paged-response";

const BASE_URL = environment.communicationService;

export async function findMyNotifications(
    type?: string,
    origin?: string,
    page?: number,
): Promise<PagedResponse<Notification>> {
    try {
        const params = new URLSearchParams();

        params.append("page", (page ?? 0).toString());

        if (type && type !== "all") params.append("type", slug(type));
        if (origin && origin !== "all") params.append("origin", slug(origin));

        const { data } = await client.get<PagedResponse<Notification>>(
            `${BASE_URL}?${params.toString()}`,
        );
        return data;
    } catch {
        throw new Error("Notifications could not be fetched");
    }
}

export async function readNotification(id: string): Promise<void> {
    try {
        await client.patch(`${BASE_URL}/${id}/read`); // Fixed template literal
    } catch {
        throw new Error("Notification could not be read");
    }
}
