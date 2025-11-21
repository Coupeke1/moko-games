import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import { Padding } from "@/components/layout/padding";
import Row from "@/components/layout/row";

interface Props {
    image: string;
    username: string;
    email: string;
    description: string;
    level: number;
    playtime: string;
}

function Image({ src }: { src: string }) {
    return (
        <section
            className="bg-cover min-w-32 min-h-28 rounded-lg bg-center"
            style={{ backgroundImage: `url(${src})` }}
        />
    )
}

function EditButton({ small }: { small: boolean; }) {
    return (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 22 22" fill="currentColor" className={`size-6 cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75 ${small ? "md:hidden" : "hidden md:block"}`}>
            <path d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712ZM19.513 8.199l-3.712-3.712-8.4 8.4a5.25 5.25 0 0 0-1.32 2.214l-.8 2.685a.75.75 0 0 0 .933.933l2.685-.8a5.25 5.25 0 0 0 2.214-1.32l8.4-8.4Z" />
            <path d="M5.25 5.25a3 3 0 0 0-3 3v10.5a3 3 0 0 0 3 3h10.5a3 3 0 0 0 3-3V13.5a.75.75 0 0 0-1.5 0v5.25a1.5 1.5 0 0 1-1.5 1.5H5.25a1.5 1.5 0 0 1-1.5-1.5V8.25a1.5 1.5 0 0 1 1.5-1.5h5.25a.75.75 0 0 0 0-1.5H5.25Z" />
        </svg>
    )
}

function Statistic({ title, value }: { title: string; value: string; }) {
    return (
        <Column gap={Gap.None}>
            <h4 className="text-sm md:text-xs text-fg-2">{title}</h4>
            <p className="text-lg md:text-md">{value}</p>
        </Column>
    )
}

export default function ProfileInformation({ image, username, email, description, level, playtime }: Props) {
    return (
        <Row justify={Justify.Between} items={Items.Stretch}>
            <Row gap={Gap.Large} items={Items.Stretch}>
                <Image src={image} />
                <Column padding={Padding.Small}>
                    <Column gap={Gap.None}>
                        <Row items={Items.Center} responsive={false}>
                            <h2 className="text-3xl font-bold">{username}</h2>
                            <EditButton small={true} />
                        </Row>

                        <h3 className="text-fg-2">{email}</h3>
                    </Column>
                    <p className="max-w-xs">{description.substring(0, 64)}{description.length > 64 ? "..." : ""}</p>
                </Column>
            </Row>

            <section className="flex flex-col md:justify-between md:items-end p-1">
                <EditButton small={false} />
                <Row gap={Gap.Large} responsive={false}>
                    <Statistic
                        title="Level"
                        value="124"
                    />

                    <Statistic
                        title="Time Played"
                        value="365h 23m"
                    />
                </Row>
            </section>
        </Row>
    )
}