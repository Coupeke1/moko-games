import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
import Section from "@/components/section";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import { useMyProfile } from "@/hooks/use-my-profile";
import AchievementCard from "@/routes/profile/components/achievement-card";
import ProfileInformation from "@/routes/profile/components/information";
import SettingsDialog from "@/routes/profile/dialogs/settings-dialog";
import { useState } from "react";
import FriendsSection from "./friends";

export default function ProfilePage() {
    const { profile, isLoading, isError } = useMyProfile();
    const [settings, setSettings] = useState(false);

    if (isLoading || profile === undefined) return <Page><LoadingState /></Page>
    if (isError) return <Page><ErrorState /></Page>

    return (
        <Page>
            <SettingsDialog profile={profile} close={() => setSettings(false)} open={settings} onChange={setSettings} />

            <Column gap={Gap.ExtraLarge}>
                <ProfileInformation
                    image={profile.image}
                    username={profile.username}
                    email={profile.email}
                    description={profile.description}
                    level={profile.statistics.level}
                    playTime={profile.statistics.playTime}
                    onEdit={() => setSettings(true)}
                />

                <FriendsSection />

                {
                    profile.modules.achievements && (
                        <Section title="Achievements">
                            <Column>
                                <AchievementCard
                                    image={"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fimg.opencritic.com%2Fgame%2F6846%2Fo%2FOynP2Fqx.jpg&f=1&nofb=1&ipt=371f8470bf154c2bf29073bc3c6ee6b8d0a1fbbfb728b1e7b20538e5266af57a"}
                                    title="The Ends and the Means"
                                    description="Discover what happened to NERO"
                                    date="7/10/23"
                                    game="Days Gone"
                                />
                            </Column>
                        </Section>
                    )
                }

                {
                    profile.modules.favourites && (
                        <Section title="Favourites">
                            <Column>
                                <AchievementCard
                                    image={"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fimg.opencritic.com%2Fgame%2F6846%2Fo%2FOynP2Fqx.jpg&f=1&nofb=1&ipt=371f8470bf154c2bf29073bc3c6ee6b8d0a1fbbfb728b1e7b20538e5266af57a"}
                                    title="The Ends and the Means"
                                    description="Discover what happened to NERO"
                                    date="7/10/23"
                                    game="Days Gone"
                                />
                            </Column>
                        </Section>
                    )
                }
            </Column>
        </Page>
    )
}