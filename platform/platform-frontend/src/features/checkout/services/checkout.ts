import { environment } from "@/config.ts";
import type { Order } from "@/features/checkout/models/order";
import { client } from "@/lib/api-client.ts";

const BASE_URL = environment.orderService;

export async function placeOrder(): Promise<string> {
    try {
        const { data } = await client.post(BASE_URL);
        return data;
    } catch {
        throw new Error("Could not place order");
    }
}

export async function verifyOrder(id: string): Promise<Order> {
    try {
        const { data } = await client.post(`${BASE_URL}/${id}/verify`);
        return data;
    } catch {
        throw new Error("Could not verify order");
    }
}

export async function findOrder(id: string): Promise<Order> {
    try {
        const { data } = await client.get<Order>(`${BASE_URL}/${id}`);
        return data;
    } catch {
        throw new Error("Order could not be fetched");
    }
}
