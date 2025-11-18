import Column from "@/components/layout/column"
import { Gap } from "@/components/layout/gap"
import NavigationBar from "@/components/navigation/bar"
import Footer from "@/components/navigation/footer"
import type { ReactNode } from "react"

interface Props {
    children: ReactNode
}

export default function Page({ children }: Props) {
    return (
        <section className="flex flex-col gap-8 justify-between min-h-screen mx-auto max-w-4xl px-4 pt-4 pb-2.5">
            <section className="flex flex-col gap-4 md:gap-12">
                <section className="w-full">
                    <NavigationBar />
                </section>

                <section className="flex flex-col mx-auto max-w-2xl w-full">
                    {children}
                </section>
            </section>

            <Footer />
        </section>
    )
}