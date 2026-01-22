import type { ReactNode } from "react";
import { Link as RouterLink } from "react-router";

interface Props {
    path: string;
    children: ReactNode;
    fullWidth?: boolean;
}

export default function LinkButton({
    path,
    children,
    fullWidth = false,
}: Props) {
    return (
        <RouterLink
            to={path}
            className={`rounded-lg bg-bg-2 hover:bg-bg-3 transition-colors duration-75 flex flex-row gap-1 h-10 items-center justify-center font-semibold disabled:cursor-not-allowed px-4 py-1 cursor-pointer ${fullWidth ? "w-full" : ""}`}
        >
            {children}
        </RouterLink>
    );
}
