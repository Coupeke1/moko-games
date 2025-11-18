import type { ReactNode } from "react";

interface Props {
    label: string;
    type?: string;
    disabled?: boolean;
    required?: boolean;
    value: string;
    onChange: (e: any) => void;
    children?: ReactNode;
}

export default function Input({ label, type = "text", disabled, required, value, onChange, children }: Props) {
    return (
        <input
            type={type}
            required={required}
            disabled={disabled}
            placeholder={label}
            className="rounded-lg bg-bg-2 h-10 px-3 placeholder:text-fg-2"
            value={value} onChange={onChange}
        >
            {children && (
                <section className="absolute right-3 top-1/2 transform -translate-y-1/2">
                    {children}
                </section>
            )}
        </input>
    )
}