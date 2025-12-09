import type { Entry } from "@/features/store/models/entry/entry.ts";

export interface Entries {
    items: Entry[];
    page: number;
    size: number;
    last: boolean;
}
