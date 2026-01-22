import type {ReactNode} from "react";

interface BigButtonProps {
    onClick?: () => void,
    disabled?: boolean,
    children: ReactNode
}

export function BigButton({onClick, disabled, children}: BigButtonProps) {
    return (
        <button
            onClick={onClick}
            type={onClick ? "button" : "submit"}
            disabled={disabled}
            className={`
            rounded-lg bg-bg-4 ${disabled ? "" : "hover:bg-valid-move transition-colors duration-75"}
            flex flex-row gap-1 items-center justify-center font-semibold 
            mt-2 px-4 py-4 disabled:cursor-not-allowed cursor-pointer
            transition-all duration-150
            text-fg text-lg
            `}
        >
            {children}
        </button>
    )
}