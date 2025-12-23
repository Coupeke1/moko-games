import Card from "@/components/cards/card";

interface Props {
    image: string;
    title: string;
    description: string;
}

export default function FavouriteCard({ image, title, description }: Props) {
    return (
        <Card
            title={title}
            image={image}
            information={<p className="truncate">{description}</p>}
        />
    );
}
