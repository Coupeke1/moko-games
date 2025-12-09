import type { Type } from "@/features/store/models/post/type.ts";

export interface Post {
    id: string;
    title: string;
    image: string;
    type: Type;
    content: string;
}
