import type { ReactNode } from "react"
import { Link, useLocation } from "react-router"

interface Props {
    title: string;
    path: string;
    children: ReactNode;
}

export default function NavigationLink({ title, path, children }: Props) {
    const location = useLocation();
    const matches: boolean = location.pathname.startsWith(path);

    return (
        <Link to={path} className={`flex flex-row items-center gap-2 text-xl cursor-pointer ${matches ? "text-fg" : "text-fg-2 hover:text-fg transition-colors duration-75"}`}>
            <h2>{children}</h2>
            <h2>{title}</h2>
        </Link>
    )
}