import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import Row from "@/components/layout/row";
import { Height } from "@/components/layout/size";
import type { ReactNode } from "react";
import { Link as RouterLink } from "react-router";
import Stack from "@/components/layout/stack";
import { Items } from "@/components/layout/items";

interface Props {
    image: string;
    title: string;
    information?: ReactNode;
    options?: ReactNode;
    footer?: ReactNode;

    height?: Height;
    href?: string;
    onClick?: () => void;
}

function Content({
    image,
    title,
    information,
    options,
    footer,
    height = Height.Medium,
    href,
    onClick,
}: Props) {
    return (
        <Column>
            <article
                className={`flex flex-col ${(href || onClick) && "group cursor-pointer"} relative overflow-hidden select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg ${height}`}
                style={{ backgroundImage: `url("${image}")` }}
            >
                <section className="absolute inset-0 bg-linear-to-b from-black/10 via-black/20 to-black/80 from-0% via-45% to-100% group-hover:bg-black/20 transition-colors duration-200 rounded-lg" />

                <section className="relative z-10">
                    <Row
                        items={Items.End}
                        justify={Justify.Between}
                        responsive={false}
                    >
                        <section className="min-w-0">
                            <Stack>
                                <h3 className="font-bold text-lg truncate">
                                    {title}
                                </h3>

                                <Row gap={Gap.Large} responsive={false}>
                                    {information && information}
                                </Row>
                            </Stack>
                        </section>

                        {options && options}
                    </Row>
                </section>
            </article>

            {footer}
        </Column>
    );
}

export default function Card(props: Props) {
    const { href, onClick } = props;

    if (!href && !onClick) return <Content {...props} />;

    if (onClick && !href)
        return (
            <button onClick={onClick}>
                <Content {...props} />
            </button>
        );

    if (href && !onClick)
        return (
            <RouterLink to={href}>
                <Content {...props} />
            </RouterLink>
        );

    return null;
}
