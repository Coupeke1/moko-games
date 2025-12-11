import CalendarIcon from "@/components/icons/calendar-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";

interface Props {
    image: string;
    title: string;
    description: string;
}

export default function FavouriteCard({ image, title, description }: Props) {
    return (
        <article
            className="flex flex-col group relative overflow-hidden cursor-pointer select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg h-32"
            style={{ backgroundImage: `url("${image}")` }}
        >
            <section className="absolute inset-0 bg-black/40 group-hover:bg-black/50 rounded-lg transition-colors duration-200" />

            <section className="relative z-10">
                <h3 className="font-bold text-lg">
                    {title.substring(0, 35)}
                    {title.length > 35 ? "..." : ""}
                </h3>
                <p>
                    {description.substring(0, 50)}
                    {description.length > 50 ? "..." : ""}
                </p>
            </section>
        </article>
    );
}
