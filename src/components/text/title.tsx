import type { ReactNode } from "react";

interface Props {
    children: ReactNode;
}

export default function Title({ children }: Props) {
    return <h3 className="text-lg sm:text-2xl">{children}</h3>;
}
