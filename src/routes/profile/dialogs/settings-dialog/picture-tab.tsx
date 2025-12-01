import Input from "@/components/inputs/input";
import Column from "@/components/layout/column";
import Image from "@/routes/profile/components/image";
interface Props {
    image: string;
    setImage: (image: string) => void;
}
export default function PictureTab({ image, setImage }: Props) {
    return (
        <Column>
            <Input
                label="Picture"
                value={image}
                onChange={(e) => setImage(e.target.value)}
            />
            <Image src={image} />
        </Column>
    );
}
