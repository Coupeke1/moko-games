import type { ReactNode } from "react"

interface Props {
    children: ReactNode
}

export default function Page({ children }: Props) {
    return (
        <div className="min-h-screen bg-bg text-fg flex flex-col">
            <main className="flex-grow flex flex-col gap-6 md:gap-12 px-4 py-6 max-w-4xl mx-auto w-full">
                {children}
            </main>
        </div>
    );
}