import type { ReactNode } from "react";

interface Props {
    onClick?: () => void,
    children: ReactNode
}

export default function BigButton({ onClick, children }: Props) {
    return (
        <button onClick={onClick} type="button" className="p-4 border-3 min-h-32 border-gray-200 dark:border-neutral-400 text-gray-600 dark:text-gray-100 rounded-lg flex items-center justify-center gap-2 cursor-pointer">
            {children}
        </button>
    )
}