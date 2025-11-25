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
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 28 28" fill="currentColor" className="size-6">
                            <path fillRule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25ZM12.75 6a.75.75 0 0 0-1.5 0v6c0 .414.336.75.75.75h4.5a.75.75 0 0 0 0-1.5h-3.75V6Z" clipRule="evenodd" />
                        </svg>

                        <p>{playtime}</p>
                    </Row>

                    <Row gap={Gap.None} items={Items.Center} responsive={false}>
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 28 28" fill="currentColor" className="size-6">
                            <path d="M4.5 6.375a4.125 4.125 0 1 1 8.25 0 4.125 4.125 0 0 1-8.25 0ZM14.25 8.625a3.375 3.375 0 1 1 6.75 0 3.375 3.375 0 0 1-6.75 0ZM1.5 19.125a7.125 7.125 0 0 1 14.25 0v.003l-.001.119a.75.75 0 0 1-.363.63 13.067 13.067 0 0 1-6.761 1.873c-2.472 0-4.786-.684-6.76-1.873a.75.75 0 0 1-.364-.63l-.001-.122ZM17.25 19.128l-.001.144a2.25 2.25 0 0 1-.233.96 10.088 10.088 0 0 0 5.06-1.01.75.75 0 0 0 .42-.643 4.875 4.875 0 0 0-6.957-4.611 8.586 8.586 0 0 1 1.71 5.157v.003Z" />
                        </svg>

                        <p>{friendCount}</p>
                    </Row>
                </Row>
            </section>
        </article>
    )
}