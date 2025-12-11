import BackIcon from "@/components/icons/back-icon";
import { Gap } from "@/components/layout/gap";
import NavigationBar from "@/components/navigation/bar";
import Footer from "@/components/navigation/footer";
import { getButtons, type Link } from "@/components/navigation/models";
import CartDialog from "@/features/cart/cart-dialog";
import { useState, type ReactNode } from "react";

interface Props {
    children: ReactNode;
}

export function getLinks(): Link[] {
    return [{ title: "Back", icon: <BackIcon />, path: "/library" }];
}

export default function Page({ children }: Props) {
    const [cart, setCart] = useState(false);

    return (
        <section className="flex flex-col gap-8 justify-between min-h-screen mx-auto max-w-4xl px-4 pt-4 pb-1.5">
            <section className="flex flex-col gap-4 md:gap-12">
                <section className="w-full">
                    <CartDialog open={cart} onChange={setCart} />
                    <NavigationBar
                        links={getLinks()}
                        buttons={getButtons(() => setCart(true))}
                    />
                </section>

                <section
                    className={`flex flex-col ${Gap.Medium} mx-auto max-w-4xl px-2 w-full`}
                >
                    {children}
                </section>
            </section>

            <Footer />
        </section>
    );
}
