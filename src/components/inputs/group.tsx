import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import type { ReactNode } from "react";

interface Props {
    label?: string;
    children: ReactNode
}

export default function InputGroup({ label, children }: Props) {
    return (
        <Column gap={Gap.None}>
            {label && <span className="font-bold pl-2">{label}</span>}
            <section className="p-4 border-3 border-gray-200 dark:border-neutral-400 rounded-lg">
                <Column>
                    {children}
                </Column>
            </section>
        </Column>
    )
}