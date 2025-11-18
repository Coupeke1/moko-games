import { Gap } from "@/components/layout/gap";
import { GridSize } from "@/components/layout/grid/size";
import type { ReactNode } from "react";

interface Props {
    gap?: Gap,
    size?: GridSize,
    children: ReactNode
}

export default function Grid({ gap = Gap.Medium, size = GridSize.Medium, children }: Props) {
    return (
        <section className={`grid ${size} ${gap}`}>
            {children}
        </section>
    )
}