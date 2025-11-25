import type { ChangeEvent } from "react";

interface Props {
    label: string;
    description?: string;
    placeholder?: string;
    disabled?: boolean;
    required?: boolean;
    value: string;
    onChange: (e: ChangeEvent<HTMLTextAreaElement>) => void
}

export default function InputBox({ label, description, placeholder, disabled, required, value, onChange }: Props) {
    return (
        <label className="flex flex-col">
            <span className="font-semibold pl-1">{label} {required ? <span>(required)</span> : <></>}</span>
            <textarea placeholder={placeholder} disabled={disabled} value={value} onChange={onChange} className="rounded-lg py-2 h-42 bg-bg-2 w-full min-h-10 px-3 placeholder:text-fg-2 disabled:text-fg-2 font-semibold resize-none" />
            {description && <span className="pl-2">{description}</span>}
        </label>
    )
}