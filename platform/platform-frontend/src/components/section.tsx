import Column from "@/components/layout/column";
import type { ReactNode } from "react";
import { Gap } from "@/components/layout/gap";

interface Props {
    title: string;
    children: ReactNode
}

export default function Section({ title, children }: Props) {
    return (
        <Column gap={Gap.None}>
            <h2 className="font-bold text-fg-2 text-lg">{title}</h2>

            <Column>
                {children}
            </Column>
        </Column>
    )
}