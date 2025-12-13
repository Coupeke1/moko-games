import InputBox from "@/components/inputs/box";
import Column from "@/components/layout/column";
import { forwardRef, useImperativeHandle, useState } from "react";

export interface AboutData {
    data: () => { description: string };
}

const AboutTab = forwardRef<AboutData, { initial: string }>(
    ({ initial }, ref) => {
        const [description, setDescription] = useState(initial);

        useImperativeHandle(ref, () => ({
            data: () => ({ description }),
        }));

        return (
            <Column>
                <InputBox
                    label="About"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
            </Column>
        );
    },
);

export default AboutTab;
