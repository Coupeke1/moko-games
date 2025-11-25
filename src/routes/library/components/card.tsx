import ClockIcon from "@/components/icons/clock-icon";
import UsersIcon from "@/components/icons/users-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";

interface Props {
    title: string;
    image: string;
    playtime: string;
    friendCount: number;
}

export default function LibraryCard({ title, image, playtime, friendCount }: Props) {
    return (
        <article className="flex flex-col group relative overflow-hidden cursor-pointer select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg h-64" style={{ backgroundImage: `url("${image}")` }}>
            <section className="absolute inset-0 bg-black/15 group-hover:bg-black/30 rounded-lg transition-colors duration-200" />

            <section className="relative z-10">
                <h3 className="font-bold text-lg">{title.substring(0, 15)}{title.length > 15 ? "..." : ""}</h3>
                <Row gap={Gap.Large} responsive={false}>
                    <Row gap={Gap.None} items={Items.Center} responsive={false}>
                        <ClockIcon />
                        <p>{playtime}</p>
                    </Row>

                    <Row gap={Gap.None} items={Items.Center} responsive={false}>
                        <UsersIcon />
                        <p>{friendCount}</p>
                    </Row>
                </Row>
            </section>
        </article>
    )
}