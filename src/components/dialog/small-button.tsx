import type { ReactNode } from "react";

interface Props {
    onClick?: () => void,
    children: ReactNode
}

export default function SmallButton({ onClick, children }: Props) {
    return (
        <button onClick={onClick} className="flex items-center justify-center font-bold hover:bg-gray-200 dark:hover:bg-neutral-800 p-2 transition-colors rounded-lg cursor-pointer">
            {children}
        </button>
    )
}