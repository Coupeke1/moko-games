import InputBox from "@/components/inputs/box";
import Column from "@/components/layout/column";

interface Props {
    description: string;
    setDescription: (description: string) => void;
}

export default function AboutTab({ description, setDescription }: Props) {
    return (
        <Column>
            <InputBox
                label="About"
                value={description}
                onChange={(e) => {
                    setDescription(e.target.value);
                }}
            />
        </Column>
    );
}
