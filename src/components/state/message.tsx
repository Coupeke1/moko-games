import type { ReactNode } from "react";

interface Props {
    pulsing?: boolean;
    children: ReactNode
}

export default function Message({ pulsing, children }: Props) {
    return (
        <p className={`text-fg-2 ${pulsing ? "animate-pulse" : ""}`}>
            {children}
        </p>
    )
}