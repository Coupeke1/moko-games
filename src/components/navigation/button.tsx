import type { ReactNode } from "react";
import { Link, useLocation } from "react-router";

interface Props {
    path: string;
    children: ReactNode;
}

export default function NavigationButton({ path, children }: Props) {
    const location = useLocation();
    const matches: boolean = location.pathname.startsWith(path);

    return (
        <Link to={path} className={`text-xl cursor-pointer ${matches ? "text-fg" : "text-fg-2 hover:text-fg transition-colors duration-75"}`}>
            {children}
        </Link>
    )
}