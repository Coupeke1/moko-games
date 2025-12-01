import type { ChangeEvent, ReactNode } from "react";

interface Props {
    label?: string;
    placeholder?: string;
    type?: string;
    min?: number;
    max?: number;
    disabled?: boolean;
    required?: boolean;
    value: string;
    onChange: (e: ChangeEvent<HTMLInputElement>) => void;
    children?: ReactNode;
}

export default function Input({
    label,
    placeholder,
    type = "text",
    min = 1,
    max = 8,
    disabled,
    required,
    value,
    onChange,
}: Props) {
    return (
        <label className="flex flex-col w-full">
            {label && <span className="font-semibold pl-1">{label}</span>}
            <input
                placeholder={placeholder}
                type={type}
                min={min}
                max={max}
                disabled={disabled}
                required={required}
                value={value}
                onChange={onChange}
                className="rounded-lg bg-bg-2 w-full h-10 px-3 placeholder:text-fg-2 disabled:text-fg-2 font-semibold"
            />
        </label>
    );
}
