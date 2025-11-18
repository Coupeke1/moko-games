import type { ReactNode } from "react";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import { Items } from "@/components/layout/items";

interface Props {
    gap?: Gap,
    justify?: Justify,
    items?: Items,
    maxHeight?: boolean,
    children: ReactNode
}

export default function Column({ gap = Gap.Medium, justify = Justify.None, items = Items.Stretch, maxHeight, children }: Props) {
    return (
        <section className={`flex flex-col ${gap} ${justify} ${items} ${maxHeight ? "min-h-screen" : ""}`}>
            {children}
        </section>
    )
}