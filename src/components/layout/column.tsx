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
    grow?: boolean;
    fullWidth?: boolean;
    children: ReactNode;
}

export default function Column({
    gap = Gap.Medium,
    justify = Justify.None,
    items = Items.Stretch,
    padding = Padding.None,
    grow,
    fullWidth: maxHeight,
    children,
}: Props) {
    return (
        <section
            className={`flex flex-col ${gap} ${padding} ${justify} ${items} ${maxHeight ? "min-h-screen" : ""} ${grow ? "grow" : ""}`}
        >
            {children}
        </section>
    );
}
