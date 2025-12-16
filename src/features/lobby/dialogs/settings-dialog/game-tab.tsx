import Column from "@/components/layout/column";
import {Gap} from "@/components/layout/gap";
import Input from "@/components/inputs/input";
import type {GameSettingsSchema} from "@/features/lobby/models/settings.ts";

interface Props {
    schema: GameSettingsSchema | null;
    loading: boolean;
    isOwner: boolean;
    values: Record<string, unknown>;
    setValue: (name: string, value: unknown) => void;
}

export default function GameTab({schema, loading, isOwner, values, setValue}: Props) {
    if (loading) return <p>Loading game settings…</p>;
    if (!schema || !schema.settings || schema.settings.length === 0)
        return <p>No game settings available.</p>;

    return (
        <Column gap={Gap.Medium}>
            {schema.settings.map((s) => {
                const current = values[s.name];

                if (s.type === "INTEGER") {
                    return (
                        <Input
                            key={s.name}
                            label={s.name}
                            disabled={!isOwner}
                            type="number"
                            value={String(current ?? s.defaultValue ?? "")}
                            onChange={(e) => setValue(s.name, Number(e.target.value))}
                        />
                    );
                }

                if (s.type === "BOOLEAN") {
                    return (
                        <label key={s.name} className="flex items-center gap-2 select-none">
                            <input
                                type="checkbox"
                                disabled={!isOwner}
                                checked={Boolean(current ?? s.defaultValue ?? false)}
                                onChange={(e) => setValue(s.name, e.target.checked)}
                            />
                            <span>{s.name}</span>
                        </label>
                    );
                }

                return (
                    <Input
                        key={s.name}
                        label={s.name}
                        disabled={!isOwner}
                        type="text"
                        value={String(current ?? s.defaultValue ?? "")}
                        onChange={(e) => setValue(s.name, e.target.value)}
                    />
                );
            })}
        </Column>
    );
}
