import MenuIcon from "@/components/icons/bar/menu-icon";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import Row from "@/components/layout/row";
import NavigationButton from "@/components/navigation/button";
import NavigationLink from "@/components/navigation/link";
import NavigationMenu from "@/components/navigation/menu";
import type { Button, Link } from "@/components/navigation/models";
import { useState } from "react";
import { Link as RouterLink } from "react-router";

interface Props {
    links: Link[];
    buttons: Button[];
}

export default function NavigationBar({ links, buttons }: Props) {
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
                    <Row gap={Gap.ExtraLarge} wrap={true}>
                        {links.map((link: Link) => (
                            <NavigationLink
                                key={link.title}
                                title={link.title}
                                path={link.path}
                            >
                                {link.icon}
                            </NavigationLink>
                        ))}
                    </Row>
                </section>

                <section className="hidden md:flex">
                    <Row gap={Gap.Medium} wrap={true}>
                        {buttons.map((button: Button) => (
                            <NavigationButton key={button.title} path="/todo">
                                {button.icon}
                            </NavigationButton>
                        ))}
                    </Row>
                </section>
            </Row>

            <NavigationMenu
                links={links}
                buttons={buttons}
                open={open}
                onClose={close}
            />
        </>
    );
}
