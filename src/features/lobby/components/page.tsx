import BackIcon from "@/components/icons/back-icon";
import { Gap } from "@/components/layout/gap";
import NavigationBar from "@/components/navigation/bar";
import Footer from "@/components/navigation/footer";
import { getButtons, type Link } from "@/components/navigation/models";
import CartDialog from "@/features/cart/cart-dialog";
import { useState, type ReactNode } from "react";

interface Props {
    largerWidth?: boolean;
    children: ReactNode;
}

export function getLinks(): Link[] {
    return [{ title: "Back", icon: <BackIcon />, path: "/library" }];
}

export default function Page({ largerWidth, children }: Props) {
    const [cart, setCart] = useState(false);

    return (
        <section
            className={`flex flex-col gap-8 justify-between min-h-screen mx-auto ${largerWidth ? "max-w-6xl" : "max-w-4xl"} px-4 pt-4 pb-1.5`}
        >
            <section className="flex flex-col gap-4 md:gap-12">
                <section className="w-full">
                    <CartDialog open={cart} onChange={setCart} />
                    <NavigationBar
                        links={getLinks()}
                        buttons={getButtons(() => setCart(true))}
                    />
                </section>

                <section
                    className={`flex flex-col ${Gap.Medium} mx-auto ${largerWidth ? "max-w-4xl" : "max-w-2xl"} w-full`}
                >
                    {children}
                </section>
            </section>

            <Footer />
        </section>
    );
}
