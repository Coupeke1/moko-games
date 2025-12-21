import Select from "@/components/inputs/select";
import type { Setting } from "@/features/lobby/models/settings";
import { camel } from "@/lib/format";

interface Props {
    setting: Setting;
    current: unknown;
    isOwner: boolean;
    changeSetting: (name: string, value: unknown) => void;
}

export default function SelectSetting({
    setting,
    current,
    isOwner,
    changeSetting,
}: Props) {
    const options: {
        value: string;
        label: string;
        default?: boolean;
    }[] = (setting.allowedValues ?? []).map((value) => {
        return {
            value: String(value),
            label: String(value),
        };
    });

    const value = String(current ?? options[0] ?? "");

    return (
        <Select
            label={camel(setting.name)}
            disabled={!isOwner}
            value={current}
            onChange={(e) => changeSetting(setting.name, e.target.value)}
            options={options}
        />
    );
}
