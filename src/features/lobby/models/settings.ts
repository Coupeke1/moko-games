export type GameSettingType = "STRING" | "INTEGER" | "BOOLEAN" | "ENUM";

export interface GameSettingDefinition {
    name: string;
    type: GameSettingType;
    required: boolean;
    defaultValue: unknown;
    min: number | null;
    max: number | null;
    allowedValues: unknown[] | null;
}

export interface GameSettingsSchema {
    settings: GameSettingDefinition[];
}