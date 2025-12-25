import Card from "@/components/cards/card";
import { Height } from "@/components/layout/size";
import type { Favourite } from "@/features/profiles/models/favourite";

interface Props {
    favourite: Favourite;
}

export default function FavouriteCard({ favourite }: Props) {
    return (
        <Card
            image={favourite.image}
            title={favourite.title}
            height={Height.Large}
        />
    );
}
