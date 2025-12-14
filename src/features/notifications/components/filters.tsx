import Select from "@/components/inputs/select";
import Grid from "@/components/layout/grid/grid";
import { GridSize } from "@/components/layout/grid/size";
import { Origin } from "@/features/notifications/models/origin";
import { Type } from "@/features/notifications/models/type";
import { useState } from "react";
import { format } from "@/lib/format";

interface Props {
    onSearch: (type: string, origin: string) => void;
}

export default function Filters({ onSearch }: Props) {
    const [type, setType] = useState<Type | "all">("all");
    const [origin, setOrigin] = useState<Origin | "all">("all");

    const handleTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const type: string = e.target.value as Type | "all";
        setType(type);
        onSearch(type, origin);
    };

    const handleOriginChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const origin: string = e.target.value as Origin | "all";
        setOrigin(origin);
        onSearch(type, origin);
    };

    return (
        <Grid size={GridSize.ExtraSmall}>
            <Select
                placeholder="Type"
                options={[
                    { label: "All Types", value: "all" },
                    ...Object.values(Type).map((type: string) => ({
                        label: format(type),
                        value: type,
                    })),
                ]}
                value={type}
                onChange={handleTypeChange}
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
                onChange={handleOriginChange}
            />
        </Grid>
    );
}
