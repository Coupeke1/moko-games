import type { ReactNode } from "react";

interface Props {
    onClick: () => void;
    children: ReactNode;
}

export default function ButtonLink({ onClick, children }: Props) {
    return (
        <button
            onClick={onClick}
            type="button"
            className="underline decoration-2 decoration-dotted text-fg-2 cursor-pointer"
        >
            {children}
        </button>
    );
}
