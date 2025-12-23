import Input from "@/components/inputs/input";
import Column from "@/components/layout/column";
import Image from "@/features/profiles/components/image";
import { forwardRef, useImperativeHandle, useState } from "react";

export interface PictureData {
    data: () => { picture: string };
}

const PictureTab = forwardRef<PictureData, { initial: string }>(
    ({ initial }, ref) => {
        const [picture, setPicture] = useState(initial);

        useImperativeHandle(ref, () => ({
            data: () => ({ picture }),
        }));

        return (
            <Column>
                <Input
                    label="Picture"
                    value={picture}
                    onChange={(e) => setPicture(e.target.value)}
                />
                <Image src={picture} />
            </Column>
        );
    },
);

export default PictureTab;
