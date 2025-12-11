import type { Category } from "@/features/cart/models/category";

export interface Entry {
    id: string;
    title: string;
    name: string;
    image: string;
    price: number;
    category: Category;
}
