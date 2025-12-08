import type { Type } from "@/models/store/post/type";

export interface Post {
    id: string;
    title: string;
    image: string;
    type: Type;
    content: string;
}
