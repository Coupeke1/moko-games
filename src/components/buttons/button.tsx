import type { ReactNode } from "react";

interface Props {
    onClick?: () => void,
    disabled?: boolean,
    children: ReactNode
    fullWidth?: boolean
}

export default function Button({ onClick, disabled, children, fullWidth = false }: Props) {
    return (
        <button
            onClick={onClick}
            type={onClick ? "button" : "submit"}
            disabled={disabled}
            className={`rounded-lg bg-bg-2 ${disabled ? "" : "hover:bg-bg-3 transition-colors duration-75"} flex flex-row gap-1 h-10 items-center justify-center font-semibold disabled:cursor-not-allowed px-4 py-1 cursor-pointer ${fullWidth ? 'w-full' : ''}`}
        >
            {children}
        </button>
    )
}