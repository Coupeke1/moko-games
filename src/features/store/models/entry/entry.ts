import type { Category } from "@/features/store/models/entry/category.ts";

export interface Entry {
    id: string;
    title: string;
    description: string;
    name: string;
    image: string;
    price: number;
    category: Category;
    popularity: number;
    purchaseCount: number;
}
