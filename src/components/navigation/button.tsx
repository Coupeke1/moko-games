import type { ReactNode } from "react";
import { Link as RouterLink, useLocation } from "react-router";
import { Gap } from "../layout/gap";

interface Props {
    title?: string;
    path?: string;
    onClick?: () => void;
    children: ReactNode;
}

export default function NavigationButton({
    title,
    path,
    onClick,
    children,
}: Props) {
    if (path !== undefined)
        return (
            <Link title={title} path={path}>
                {children}
            </Link>
        );
    else if (onClick !== undefined)
        return (
            <Button title={title} onClick={onClick}>
                {children}
            </Button>
        );
}

function Button({
    title,
    onClick,
    children,
}: {
    title: string;
    onClick: () => void;
    children: ReactNode;
}) {
    return (
        <button
            onClick={onClick}
            className="text-xl cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75 flex flex-row items-center gap-2"
        >
            <h2>{children}</h2>
            <h2>{title}</h2>
        </button>
    );
}

function Link({
    title,
    path,
    children,
}: {
    title?: string;
    path: string;
    children: ReactNode;
}) {
    const location = useLocation();
    const matches: boolean = path.startsWith(location.pathname);

    return (
        <RouterLink
            to={path}
            className={`text-xl cursor-pointer flex flex-row items-center ${Gap.Medium} ${matches ? "text-fg" : `text-fg-2 hover:text-fg transition-colors duration-75`}`}
        >
            <h2>{children}</h2>
            {title && <h2>{title}</h2>}
        </RouterLink>
    );
}
