import type { ReactNode } from "react";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import { Items } from "@/components/layout/items";

interface Props {
    gap?: Gap;
    justify?: Justify;
    items?: Items;
    wrap?: boolean;
    children: ReactNode;
}

export default function Row({ gap = Gap.Medium, justify = Justify.None, items = Items.Start, wrap, children }: Props) {
    return (
        <section className={`flex flex-row ${gap} ${justify} ${items} ${wrap ? "flex-wrap" : ""}`}>
            {children}
        </section>
    )
}