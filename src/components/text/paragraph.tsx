import type { ReactNode } from "react";

interface Props {
    children: ReactNode;
}

export default function Paragraph({ children }: Props) {
    return <p className="text-fg-2">{children}</p>;
}
