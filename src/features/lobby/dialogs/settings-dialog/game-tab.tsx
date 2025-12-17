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
    if (!schema || schema.settings.length === 0) return <p>No game settings available.</p>;

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
                            value={String(current ?? "")}
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
                                checked={Boolean(current ?? false)}
                                onChange={(e) => setValue(s.name, e.target.checked)}
                            />
                            <span>{s.name}</span>
                        </label>
                    );
                }

                if (s.type === "ENUM") {
                    const options = (s.allowedValues ?? []).map(String);
                    const value = String(current ?? options[0] ?? "");
                    return (
                        <label key={s.name} className="flex flex-col gap-1">
                            <span className="text-sm">{s.name}</span>
                            <select
                                disabled={!isOwner}
                                value={value}
                                onChange={(e) => setValue(s.name, e.target.value)}
                                className="px-3 py-2 rounded-md bg-bg border border-border"
                            >
                                {options.map((opt) => (
                                    <option key={opt} value={opt}>
                                        {opt}
                                    </option>
                                ))}
                            </select>
                        </label>
                    );
                }

                return (
                    <Input
                        key={s.name}
                        label={s.name}
                        disabled={!isOwner}
                        type="text"
                        value={String(current ?? "")}
                        onChange={(e) => setValue(s.name, e.target.value)}
                    />
                );
            })}
        </Column>
    );
}