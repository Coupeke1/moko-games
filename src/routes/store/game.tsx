import CartIcon from "@/components/icons/bar/cart-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Justify } from "@/components/layout/justify";
import Page from "@/components/layout/page";
import Row from "@/components/layout/row";
import Section from "@/components/section";
import State from "@/components/state/state";
import Statistic from "@/components/statistic";
import Paragraph from "@/components/text/paragraph";
import { useEntry } from "@/hooks/use-entry";
import { format } from "@/models/store/category";
import Posts from "@/routes/store/components/posts";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router";

export default function StoreGamePage() {
    const navigate = useNavigate();
    const params = useParams();
    const id = params.id;

    useEffect(() => {
        if (!id) navigate("/store");
    }, [id, navigate]);

    const { entry, loading, error } = useEntry(id!);

    return (
        <Page>
            <State data={entry} loading={loading} error={error} />

            {entry && (
                <Column gap={Gap.Large}>
                    <article
                        className="flex flex-col justify-end relative overflow-hidden select-none bg-cover bg-center px-3 py-2 rounded-lg h-30"
                        style={{ backgroundImage: `url("${entry.image}")` }}
                    >
                        <section className="absolute inset-0 bg-linear-to-b from-black/20 via-black/60 to-black/80 from-0% via-45% to-100% rounded-lg" />

                        <section className="relative z-10">
                            <Row justify={Justify.Between} responsive={false}>
                                <h3 className="font-bold text-2xl">
                                    {entry.title.substring(0, 15)}
                                    {entry.title.length > 15 ? "..." : ""}
                                </h3>

                                <Row gap={Gap.Small} responsive={false}>
                                    <button className="cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75">
                                        <CartIcon big={true} />
                                    </button>
                                </Row>
                            </Row>
                        </section>
                    </article>

                    <Row gap={Gap.Large} responsive={false}>
                        <Statistic
                            title="Price"
                            value={`€${entry.price.toString()}`}
                        />
                        <Statistic
                            title="Category"
                            value={format(entry.category)}
                        />
                        <Statistic
                            title="Purchases"
                            value={entry.purchaseCount.toString()}
                        />
                    </Row>

                    <Paragraph>{entry.description}</Paragraph>

                    <Section title="Posts">
                        <Posts entry={entry} />
                    </Section>
                </Column>
            )}
        </Page>
    );
}
