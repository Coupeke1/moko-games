import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Row from "@/components/layout/row";
import { Size } from "@/components/layout/size";
import type { ReactNode } from "react";
import { Link as RouterLink } from "react-router";

interface Props {
    image: string;
    title: string;
    information?: ReactNode;
    footer?: ReactNode;

    height?: Size;
    href?: string;
}

function Content({ image, title, information, footer, height, href }: Props) {
    return (
        <Column>
            <article
                className={`flex flex-col ${href && "group"} relative overflow-hidden select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg ${height}`}
                style={{ backgroundImage: `url("${image}")` }}
            >
                <section className="absolute inset-0 bg-linear-to-b from-black/10 via-black/20 to-black/80 from-0% via-45% to-100% group-hover:bg-black/20 transition-colors duration-200 rounded-lg" />

                <section className="relative z-10">
                    <h3 className="font-bold text-lg">
                        {title.substring(0, 15)}
                        {title.length > 15 ? "..." : ""}
                    </h3>

                    <Row gap={Gap.Large} responsive={false}>
                        {information && information}
                    </Row>
                </section>
            </article>

            {footer}
        </Column>
    );
}

export default function Card(props: Props) {
    const { href } = props;

    if (!href) return <Content {...props} />;

    return (
        <RouterLink to={href}>
            <Content {...props} />
        </RouterLink>
    );
}
