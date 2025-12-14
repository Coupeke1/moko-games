import Select from "@/components/inputs/select";
import Grid from "@/components/layout/grid/grid";
import { GridSize } from "@/components/layout/grid/size";
import { useParams } from "@/features/notifications/hooks/use-params";
import { Origin } from "@/features/notifications/models/origin";
import { Type } from "@/features/notifications/models/type";
import { format, slug } from "@/lib/format";

export default function Filters() {
    const { type, setType, origin, setOrigin } = useParams();

    return (
        <Grid size={GridSize.ExtraSmall}>
            <Select
                placeholder="Type"
                options={[
                    { label: "All Types", value: "all" },
                    ...Object.values(Type).map((type) => ({
                        label: format(type),
                        value: slug(type),
                    })),
                ]}
                value={type}
                onChange={(e) => setType(e.target.value as Type | "all")}
            />

            <Select
                placeholder="Origin"
                options={[
                    { label: "All Origins", value: "all" },
                    ...Object.values(Origin).map((origin) => ({
                        label: format(origin),
                        value: slug(origin),
                    })),
                ]}
                value={origin}
                onChange={(e) => setOrigin(e.target.value as Origin | "all")}
            />
        </Grid>
    );
}
