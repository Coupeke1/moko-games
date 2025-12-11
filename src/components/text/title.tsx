import type { ReactNode } from "react";

interface Props {
    big?: boolean;
    children: ReactNode;
}

export default function Title({ big = true, children }: Props) {
    return (
        <h3
            className={`${big ? "text-lg sm:text-2xl" : "text-md sm:text-lg"} font-bold`}
        >
            {children}
        </h3>
    );
}
