import type { ReactNode } from "react";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import { Items } from "@/components/layout/items";

interface Props {
    gap?: Gap,
    justify?: Justify,
    items?: Items,
    children: ReactNode
}

export default function Row({ gap = Gap.Medium, justify = Justify.None, items = Items.Start, children }: Props) {
    return (
        <section className={`flex flex-row ${gap} ${justify} ${items}`}>
            {children}
        </section>
    )
}