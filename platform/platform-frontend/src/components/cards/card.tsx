import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import Row from "@/components/layout/row";
import { Height } from "@/components/layout/size";
import Stack from "@/components/layout/stack";
import type { ReactNode } from "react";
import { Link as RouterLink } from "react-router";

interface Props {
    image?: string;
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
    const CardBody = (
        <article
            className={`flex flex-col ${
                (href || onClick) &&
                `group cursor-pointer select-none ${
                    image
                        ? "bg-cover bg-center hover:bg-bg-3 transition-colors duration-75"
                        : "hover:bg-bg-3 transition-colors duration-75"
                }`
            } bg-bg-2 relative overflow-hidden justify-end px-4 py-2 rounded-lg ${height}`}
            style={{ backgroundImage: image ? `url("${image}")` : undefined }}
        >
            {image && (
                <section className="absolute inset-0 bg-linear-to-b from-black/10 via-black/20 to-black/80 from-0% via-45% to-100% group-hover:bg-black/20 transition-colors duration-200 rounded-lg" />
            )}

            <section className="relative z-10">
                <Row
                    items={Items.End}
                    justify={Justify.Between}
                    responsive={false}
                >
                    <section className="min-w-0">
                        <Stack items={Items.Start}>
                            <h3 className="font-bold text-lg truncate">
                                {title}
                            </h3>

                            {information && (
                                <section
                                    className={`flex flex-row ${Gap.Medium} min-w-0`}
                                >
                                    {information}
                                </section>
                            )}
                        </Stack>
                    </section>

                    {options && options}
                </Row>
            </section>
        </article>
    );

    return (
        <Column>
            {href ? (
                <RouterLink to={href}>{CardBody}</RouterLink>
            ) : onClick ? (
                <button onClick={onClick} className="text-left">
                    {CardBody}
                </button>
            ) : (
                CardBody
            )}

            {footer}
        </Column>
    );
}
export default function Card(props: Props) {
    return <Content {...props} />;
}
