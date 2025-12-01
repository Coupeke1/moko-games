import SmallButton from "@/components/dialog/small-button";
import CloseIcon from "@/components/icons/bar/close-icon";
import NavigationLink from "@/components/navigation/link";
import type { Button, Link } from "@/components/navigation/models";

interface Props {
    links: Link[];
    buttons: Button[];
    open: boolean;
    onClose: () => void;
}

export default function NavigationMenu({
    links,
    buttons,
    open,
    onClose,
}: Props) {
    return (
        <>
            {open && (
                <section className="fixed inset-0 bg-black/50 z-40 md:hidden" />
            )}

            <aside
                className={`
                fixed top-0 left-0 h-full w-64 bg-bg z-50 transform transition-transform duration-300 ease-in-out md:hidden
                ${open ? "translate-x-0" : "-translate-x-full"}
            `}
            >
                <section className="p-4 border-b border-bg-2 flex items-center justify-between">
                    <h2 className="text-lg font-semibold">Moko</h2>

                    <SmallButton onClick={onClose}>
                        <CloseIcon />
                    </SmallButton>
                </section>

                <section className="flex flex-col gap-4 p-4 border-b border-bg-2">
                    {links.map((link: Link) => (
                        <NavigationLink
                            key={link.title}
                            title={link.title}
                            path={link.path}
                        >
                            {link.icon}
                        </NavigationLink>
                    ))}
                </section>

                <section className="flex flex-col gap-4 p-4">
                    {buttons.map((button: Button) => (
                        <NavigationLink
                            key={button.title}
                            title={button.title}
                            path="/"
                        >
                            {button.icon}
                        </NavigationLink>
                    ))}
                </section>
            </aside>
        </>
    );
}
