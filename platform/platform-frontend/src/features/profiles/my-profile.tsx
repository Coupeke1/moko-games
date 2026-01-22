import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import ProfileAchievements from "@/features/profiles/components/achievements";
import ProfileFavourites from "@/features/profiles/components/favourites";
import ProfileInformation from "@/features/profiles/components/information";
import SettingsDialog from "@/features/profiles/dialogs/settings-dialog/settings-dialog";
import { useMyAchievements } from "@/features/profiles/hooks/use-my-achievements";
import { useMyFavourites } from "@/features/profiles/hooks/use-my-favourites";
import { useMyProfile } from "@/features/profiles/hooks/use-my-profile";
import { useAuthStore } from "@/stores/auth-store.ts";
import { useState } from "react";

function Achievements() {
    const { achievements, loading, error } = useMyAchievements();

    return (
        <ProfileAchievements
            achievements={achievements}
            loading={loading}
            error={error}
        />
    );
}

function Favourites() {
    const { favourites, loading, error } = useMyFavourites();

    return (
        <ProfileFavourites
            favourites={favourites}
            loading={loading}
            error={error}
        />
    );
}

export default function MyProfilePage() {
    const { profile, loading, error } = useMyProfile();
    const [settings, setSettings] = useState(false);

    const logout = useAuthStore((s) => s.logout);

    return (
        <Page>
            <State
                loading={loading}
                error={error}
                empty={!profile}
                message="Profile not found"
            >
                {profile && (
                    <>
                        <SettingsDialog
                            profile={profile}
                            close={() => setSettings(false)}
                            open={settings}
                            onChange={setSettings}
                        />

                        <Column gap={Gap.ExtraLarge}>
                            <ProfileInformation
                                image={profile.image}
                                username={profile.username}
                                email={profile.email}
                                description={profile.description}
                                level={profile.statistics.level}
                                playTime={profile.statistics.playTime}
                                onEdit={() => setSettings(true)}
                                onLogout={logout}
                            />

                            {profile.modules.achievements && <Achievements />}
                            {profile.modules.favourites && <Favourites />}
                        </Column>
                    </>
                )}
            </State>
        </Page>
    );
}
