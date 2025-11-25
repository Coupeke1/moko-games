import type { ReactNode } from "react";

interface Props {
    onSubmit: (event: React.FormEvent<HTMLFormElement>) => void | Promise<void>;
    children: ReactNode;
}

export default function Form({ onSubmit, children }: Props) {
    return (
        <form onSubmit={onSubmit} className="flex flex-col gap-2 justify-between grow">
            {children}
        </form>
    )
}