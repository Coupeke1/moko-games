import type { ReactNode } from "react";
import { Link as RouterLink, useLocation } from "react-router";

interface Props {
    path?: string;
    onClick?: () => void;
    children: ReactNode;
}

export default function NavigationButton({ path, onClick, children }: Props) {
    if (path !== undefined) return <Link path={path}>{children}</Link>
    else if (onClick !== undefined) return <Button onClick={onClick}>{children}</Button>
}

function Button({ onClick, children }: { onClick: () => void, children: ReactNode }) {
    return (
        <button onClick={onClick} className="text-xl cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75">
            {children}
        </button>
    )
}

function Link({ path, children }: { path: string, children: ReactNode }) {
    const location = useLocation();
    const matches: boolean = location.pathname.startsWith(path);

    return (
        <RouterLink to={path} className={`text-xl cursor-pointer ${matches ? "text-fg" : "text-fg-2 hover:text-fg transition-colors duration-75"}`}>
            {children}
        </RouterLink>
    );
}