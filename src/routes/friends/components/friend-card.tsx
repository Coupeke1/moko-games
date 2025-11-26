import ClockIcon from "@/components/icons/clock-icon";
import LevelIcon from "@/components/icons/level-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import type { Profile } from "@/models/profile/profile";

interface Props {
    profile: Profile;
}

export default function FriendCard({ profile }: Props) {
    return (
        <article className="flex flex-col group relative overflow-hidden cursor-pointer select-none justify-end bg-cover bg-center px-4 py-2 rounded-lg h-48" style={{ backgroundImage: `url("${profile.image}")` }}>
            <section className="absolute inset-0 bg-black/30 group-hover:bg-black/40 rounded-lg transition-colors duration-200" />

            <section className="relative z-10">
                <h3 className="font-bold text-lg">{profile.username.substring(0, 15)}{profile.username.length > 15 ? "..." : ""}</h3>
                <Row gap={Gap.Large} responsive={false}>
                    <Row gap={Gap.None} items={Items.Center} responsive={false}>
                        <LevelIcon />
                        <p>{profile.statistics.level}</p>
                    </Row>

                    <Row gap={Gap.None} items={Items.Center} responsive={false}>
                        <ClockIcon />
                        <p>{profile.statistics.playTime}</p>
                    </Row>
                </Row>
            </section>
        </article>
    )
}