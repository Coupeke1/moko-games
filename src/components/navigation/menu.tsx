import CartIcon from "@/components/icons/bar/cart-icon";
import ChatIcon from "@/components/icons/bar/chat-icon";
import CloseIcon from "@/components/icons/bar/close-icon";
import LibraryIcon from "@/components/icons/bar/library-icon";
import NotificationsIcon from "@/components/icons/bar/notifications-icon";
import ProfileIcon from "@/components/icons/bar/profile-icon";
import StoreIcon from "@/components/icons/bar/store-icon";
import NavigationLink from "@/components/navigation/link";
import SmallButton from "../dialog/small-button";

interface Props {
    open: boolean;
    onClose: () => void;
}

export default function NavigationMenu({ open, onClose }: Props) {
    return (
        <>
            {open && (
                <section
                    className="fixed inset-0 bg-black/50 z-40 md:hidden"
                />
            )}

            <aside className={`
                fixed top-0 left-0 h-full w-64 bg-bg z-50 transform transition-transform duration-300 ease-in-out md:hidden
                ${open ? 'translate-x-0' : '-translate-x-full'}
            `}>
                <section className="p-4 border-b border-bg-2 flex items-center justify-between">
                    <h2 className="text-lg font-semibold">Moko</h2>

                    <SmallButton onClick={onClose}>
                        <CloseIcon />
                    </SmallButton>
                </section>

                <section className="flex flex-col gap-4 p-4 border-b border-bg-2">
                    <NavigationLink
                        title="Store"
                        path="/store"
                    >
                        <StoreIcon />
                    </NavigationLink>

                    <NavigationLink
                        title="Library"
                        path="/library"
                    >
                        <LibraryIcon />
                    </NavigationLink>

                    <NavigationLink
                        title="Profile"
                        path="/profile"
                    >
                        <ProfileIcon />
                    </NavigationLink>
                </section>

                <section className="flex flex-col gap-4 p-4">
                    <NavigationLink title="Notifications" path="/notifications">
                        <NotificationsIcon />
                    </NavigationLink>

                    <NavigationLink title="Chat" path="/chat">
                        <ChatIcon />
                    </NavigationLink>

                    <NavigationLink title="Cart" path="/cart">
                        <CartIcon />
                    </NavigationLink>
                </section>
            </aside>
        </>
    )
}