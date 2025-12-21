import Select from "@/components/inputs/select";
import type { Setting } from "@/features/lobby/models/settings";
import { camel } from "@/lib/format";

interface Props {
    setting: Setting;
    current: unknown;
    isOwner: boolean;
    changeSetting: (name: string, value: unknown) => void;
}

export default function BooleanSetting({
    setting,
    current,
    isOwner,
    changeSetting,
}: Props) {
    return (
        <Select
            label={camel(setting.name)}
            disabled={!isOwner}
            value={
                current
                    ? current === true
                        ? "enabled"
                        : "disabled"
                    : "disabled"
            }
            onChange={(e) => changeSetting(setting.name, e.target.value)}
            options={[
                {
                    label: "Disabled",
                    value: "disabled",
                },
                {
                    label: "Enabled",
                    value: "enabled",
                },
            ]}
        />
    );
}
