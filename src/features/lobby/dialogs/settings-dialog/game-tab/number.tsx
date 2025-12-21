import Input from "@/components/inputs/input";
import type { Setting } from "@/features/lobby/models/settings";
import { camel } from "@/lib/format";

interface Props {
    setting: Setting;
    current: unknown;
    isOwner: boolean;
    changeSetting: (name: string, value: unknown) => void;
}

export default function NumberSetting({
    setting,
    current,
    isOwner,
    changeSetting,
}: Props) {
    return (
        <Input
            key={setting.name}
            label={camel(setting.name)}
            disabled={!isOwner}
            type="number"
            value={String(current ?? "")}
            onChange={(e) =>
                changeSetting(setting.name, Number(e.target.value))
            }
        />
    );
}
