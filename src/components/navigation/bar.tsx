import CartIcon from "@/components/icons/bar/cart-icon";
import ChatIcon from "@/components/icons/bar/chat-icon";
import LibraryIcon from "@/components/icons/bar/library-icon";
import MenuIcon from "@/components/icons/bar/menu-icon";
import NotificationsIcon from "@/components/icons/bar/notifications-icon";
import ProfileIcon from "@/components/icons/bar/profile-icon";
import StoreIcon from "@/components/icons/bar/store-icon";
import UsersIcon from "@/components/icons/users-icon";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import Row from "@/components/layout/row";
import NavigationButton from "@/components/navigation/button";
import NavigationLink from "@/components/navigation/link";
import NavigationMenu from "@/components/navigation/menu";
import { useState } from "react";
import { Link as RouterLink } from "react-router";

export default function NavigationBar() {
    const [open, setOpen] = useState(false);
    const toggle = () => setOpen(!open);
    const close = () => setOpen(false);

    return (
        <>
            <Row gap={Gap.Large} justify={Justify.Between}>
                <section className="flex flex-row gap-2 justify-between w-full items-center md:hidden">
                    <RouterLink to="/">
                        <h2 className="text-lg font-semibold">Moko</h2>
                    </RouterLink>

                    <NavigationButton onClick={toggle}>
                       <MenuIcon />
                    </NavigationButton>
                </section>

                <section className="hidden md:flex">
                    <Row gap={Gap.ExtraLarge} wrap={true} >
                        <NavigationLink title="Store" path="/store">
                            <StoreIcon />
                        </NavigationLink>

                        <NavigationLink title="Library" path="/library">
                            <LibraryIcon />
                        </NavigationLink>

                        <NavigationLink title="Profile" path="/profile">
                            <ProfileIcon />
                        </NavigationLink>

                        <NavigationLink title="Friends" path="/friends">
                            <UsersIcon />
                        </NavigationLink>
                    </Row>
                </section>

                <section className="hidden md:flex">
                    <Row gap={Gap.Medium} wrap={true}>
                        <NavigationButton path="/notifications">
                            <NotificationsIcon />
                        </NavigationButton>

                        <NavigationButton path="/chat">
                            <ChatIcon />
                        </NavigationButton>

                        <NavigationButton path="/cart">
                            <CartIcon />
                        </NavigationButton>
                    </Row>
                </section>
            </Row>

            <NavigationMenu open={open} onClose={close} />
        </>
    )
}