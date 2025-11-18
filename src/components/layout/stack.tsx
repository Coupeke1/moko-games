import Column from "@/components/layout/column";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import type { ReactNode } from "react";

interface Props {
    justify?: Justify,
    items?: Items,
    children: ReactNode
}

export default function Stack({ justify = Justify.None, items = Items.Stretch, children }: Props) {
    return (
        <Column justify={justify} items={items}>
            {children}
        </Column>
    )
}