import type { ReactNode } from "react";

interface Props {
    children: ReactNode;
}

export default function Page({ children }: Props) {
    return (
        <section className="flex flex-col gap-8 justify-center mx-auto max-w-2xl pt-4">
            {children}
        </section>
    );
}
