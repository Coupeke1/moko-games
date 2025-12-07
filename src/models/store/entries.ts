import type { Entry } from "@/models/store/entry";

export interface Entries {
    items: Entry[];
    page: number;
    size: number;
    last: boolean;
}
