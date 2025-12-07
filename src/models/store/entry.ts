import type { Category } from "@/models/store/category";

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
