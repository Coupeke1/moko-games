import type { ReactNode } from "react"

interface Props {
    children: ReactNode
}

export default function Page({ children }: Props) {
    return (
        <section className="flex flex-col gap-8 justify-center mx-auto max-w-2xl px-4 pt-4 pb-1.5">
            {children}
        </section>
    )
}