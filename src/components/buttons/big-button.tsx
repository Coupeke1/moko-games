import type { ReactNode } from "react";

interface Props {
    onClick?: () => void,
    disabled?: boolean,
    children: ReactNode
}

export default function BigButton({ onClick, disabled, children }: Props) {
    return (
        <button
            onClick={onClick}
            type={onClick ? "button" : "submit"}
            disabled={disabled}
            className={`rounded-lg bg-bg-2 ${disabled ? "" : "hover:bg-bg-3 transition-colors duration-75"} flex flex-row gap-1 items-center justify-center font-semibold disabled:cursor-not-allowed px-4 py-1 cursor-pointer`}
        >
            {children}
        </button>
    )
}