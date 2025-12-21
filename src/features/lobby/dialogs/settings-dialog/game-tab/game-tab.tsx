import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import State from "@/components/state/state";
import type { Game } from "@/features/games/models/game";
import BooleanSetting from "@/features/lobby/dialogs/settings-dialog/game-tab/boolean";
import NumberSetting from "@/features/lobby/dialogs/settings-dialog/game-tab/number";
import SelectSetting from "@/features/lobby/dialogs/settings-dialog/game-tab/select";
import TextSetting from "@/features/lobby/dialogs/settings-dialog/game-tab/text";
import { useSchema } from "@/features/lobby/hooks/use-schema";
import { getDefaultFromSchema } from "@/features/lobby/lib/merge";
import type { Lobby } from "@/features/lobby/models/lobby";
import type { Setting } from "@/features/lobby/models/settings";
import { Type } from "@/features/lobby/models/type";
import { useEffect } from "react";

interface Props {
    lobby: Lobby;
    game: Game;
    isOwner: boolean;
    settings: Record<string, unknown>;
    setSettings: (settings: Record<string, unknown>) => void;
    changeSetting: (name: string, value: unknown) => void;
}

export default function GameTab({
    lobby,
    game,
    isOwner,
    settings,
    setSettings,
    changeSetting,
}: Props) {
    const { schema, loading, error } = useSchema(game.id);

    useEffect(() => {
        if (!schema) return;
        const data = lobby.settings ?? {};
        setSettings(
            schema ? { ...getDefaultFromSchema(schema), ...data } : data,
        );
    }, [lobby.settings, schema, setSettings]);

    return (
        <State
            loading={loading}
            error={error}
            empty={!game && !schema}
            message="No settings"
        >
            {game && schema && (
                <Column gap={Gap.Medium}>
                    {schema.settings.map((setting: Setting) => {
                        const current = settings[setting.name];

                        switch (setting.type) {
                            case Type.Integer:
                                return (
                                    <NumberSetting
                                        setting={setting}
                                        current={current}
                                        isOwner={isOwner}
                                        changeSetting={changeSetting}
                                    />
                                );
                            case Type.Boolean:
                                return (
                                    <BooleanSetting
                                        setting={setting}
                                        current={current}
                                        isOwner={isOwner}
                                        changeSetting={changeSetting}
                                    />
                                );

                            case Type.Enum:
                                return (
                                    <SelectSetting
                                        setting={setting}
                                        current={current}
                                        isOwner={isOwner}
                                        changeSetting={changeSetting}
                                    />
                                );
                            default:
                                return (
                                    <TextSetting
                                        setting={setting}
                                        current={current}
                                        isOwner={isOwner}
                                        changeSetting={changeSetting}
                                    />
                                );
                        }
                    })}
                </Column>
            )}
        </State>
    );
}
