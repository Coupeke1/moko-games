import Select from "@/components/inputs/select";
import Grid from "@/components/layout/grid/grid";
import { GridSize } from "@/components/layout/grid/size";
import type { ReadFilter } from "@/features/notifications/models/notification.ts";
import { format, Origin } from "@/features/notifications/models/type";

interface Props {
    status: ReadFilter;
    onStatusChange: (status: ReadFilter) => void;
    type: Origin | "all";
    onTypeChange: (type: Origin | "all") => void;
}

export default function NotificationFilters({
    status: type,
    onStatusChange: onTypeChange,
    type: origin,
    onTypeChange: onOriginChange,
}: Props) {
    return (
        <Grid size={GridSize.ExtraSmall}>
            <Select
                placeholder="Type"
                options={[
                    { label: "All Types", value: "all" },
                    { label: "Unread", value: "unread" },
                    { label: "Read", value: "read" },
                ]}
                value={type}
                onChange={(e) => onTypeChange(e.target.value as ReadFilter)}
            />

            <Select
                placeholder="Origin"
                options={[
                    { label: "All Origins", value: "all" },
                    ...Object.values(Origin).map((origin: string) => ({
                        label: format(origin),
                        value: origin,
                    })),
                ]}
                value={origin}
                onChange={(e) =>
                    onOriginChange(e.target.value as Origin | "all")
                }
            />
        </Grid>
    );
}
