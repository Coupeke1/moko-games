import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";

interface Props {
    title: string;
    value: string;
}

export default function Statistic({ title, value }: Props) {
    return (
        <Column gap={Gap.None}>
            <h4 className="text-sm md:text-xs text-fg-2">{title}</h4>
            <p className="text-lg text-right md:text-md">{value}</p>
        </Column>
    );
}
