import type { FormEvent, ReactNode } from "react";
import { Gap } from "@/components/layout/gap";

interface Props {
    submit: (event: FormEvent<HTMLFormElement>) => void;
    children: ReactNode;
}

export default function Form({ submit, children }: Props) {
    return (
        <form onSubmit={submit} className={`flex flex-col ${Gap.Medium}`}>
            {children}
        </form>
    );
}
