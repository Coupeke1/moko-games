import type { ChangeEvent } from "react";

interface Props {
    label: string;
    disabled?: boolean;
    required?: boolean;
    value: string;
    onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
    options: { value: string; label: string; default?: boolean }[];
}

export default function Select({ label, disabled, required, value, onChange, options }: Props) {
    return (
        <label className="flex flex-col w-full">
            {label && <span className="font-semibold pl-1">{label} {required ? <span>(required)</span> : <></>}</span>}
            <section className="relative flex items-center min-w-32 w-full">
                <section className="absolute left-3 top-1/2 transform -translate-y-1/2 pointer-events-none">
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        strokeWidth={2}
                        stroke="currentColor"
                        className="w-4 h-4 text-gray-500"
                    >
                        <path strokeLinecap="round" strokeLinejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5" />
                    </svg>
                </section>

                <select
                    disabled={disabled}
                    required={required}
                    value={value}
                    onChange={onChange}
                    className="rounded-lg bg-bg-2 w-full h-10 px-3 appearance-none pl-10 text-fg-2"
                >
                    <option value="" disabled>
                        {label}
                    </option>
                    {options.map((option) => (
                        <option
                            key={String(option.value)}
                            value={String(option.value)}
                        >
                            {option.label}
                        </option>
                    ))}
                </select>
            </section>
        </label>
    )
}