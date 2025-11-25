import EditIcon from "@/components/icons/edit-icon";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import { Padding } from "@/components/layout/padding";
import Row from "@/components/layout/row";
import { Link as RouterLink } from "react-router";
import Image from "@/routes/profile/components/image";

interface Props {
    image: string;
    username: string;
    email: string;
    description: string;
    level: number;
    playTime: string;
}

function Statistic({ title, value }: { title: string; value: string; }) {
    return (
        <Column gap={Gap.None}>
            <h4 className="text-sm md:text-xs text-fg-2">{title}</h4>
            <p className="text-lg text-right md:text-md">{value}</p>
        </Column>
    )
}

export default function ProfileInformation({ image, username, email, description, level, playTime }: Props) {
    return (
        <Row justify={Justify.Between} items={Items.Stretch}>
            <Row gap={Gap.Large} items={Items.Stretch}>
                <Image src={image} />

                <Column padding={Padding.Small}>
                    <Column gap={Gap.None}>
                        <Row items={Items.Center} responsive={false}>
                            <h2 className="text-3xl font-bold">{username}</h2>

                            <RouterLink to="/profile/settings">
                                <EditIcon small={true} />
                            </RouterLink>
                        </Row>

                        <h3 className="text-fg-2">{email}</h3>
                    </Column>

                    <p className="max-w-xs">{description.substring(0, 64)}{description.length > 64 ? "..." : ""}</p>
                </Column>
            </Row>

            <section className="flex flex-col md:justify-between md:items-end p-1">
                <RouterLink to="/profile/settings">
                    <EditIcon small={false} />
                </RouterLink>

                <Row gap={Gap.Large} responsive={false}>
                    <Statistic
                        title="Level"
                        value={level.toString()}
                    />

                    <Statistic
                        title="Time Played"
                        value={playTime.toString()}
                    />
                </Row>
            </section>
        </Row>
    )
}