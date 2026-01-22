import type { Type } from "@/features/lobby/models/type";

export interface Setting {
    name: string;
    type: Type;
    required: boolean;
    defaultValue: unknown;
    min: number | null;
    max: number | null;
    allowedValues: unknown[] | null;
}

export interface Schema {
    settings: Setting[];
}
