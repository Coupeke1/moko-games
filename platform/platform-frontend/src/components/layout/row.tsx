import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import { Padding } from "@/components/layout/padding";
import type { ReactNode } from "react";

interface Props {
    gap?: Gap;
    justify?: Justify;
    items?: Items;
    padding?: Padding;
    wrap?: boolean;
    responsive?: boolean;
    children: ReactNode;
}

export default function Row({ gap = Gap.Medium, justify = Justify.None, items = Items.Start, padding = Padding.None, wrap, responsive = true, children }: Props) {
    return (
        <section className={`flex ${responsive ? "flex-col md:flex-row" : "flex-row"} ${padding} ${gap} ${justify} ${items} ${wrap ? "flex-wrap" : ""}`}>
            {children}
        </section>
    )
}