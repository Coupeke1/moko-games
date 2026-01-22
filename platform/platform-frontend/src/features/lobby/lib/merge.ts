import type { Schema } from "@/features/lobby/models/settings";

export function getDefaultFromSchema(schema: Schema): Record<string, unknown> {
    const out: Record<string, unknown> = {};
    for (const setting of schema.settings)
        out[setting.name] = setting.defaultValue;
    return out;
}
