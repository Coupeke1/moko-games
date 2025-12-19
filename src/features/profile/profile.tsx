import Column from "@/components/layout/column";
import {Gap} from "@/components/layout/gap";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import {useProfile} from "@/features/profile/hooks/use-profile.ts";
import ProfileInformation from "@/features/profile/components/information";
import SettingsDialog from "@/features/profile/dialogs/settings-dialog/settings-dialog";
import {useState} from "react";
import ProfileAchievements from "@/features/profile/components/achievements";
import ProfileFavourites from "@/features/profile/components/favourites";
import {useAuthStore} from "@/stores/auth-store.ts";

export default function ProfilePage() {
    const {profile, loading, error} = useProfile();
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

                            {profile.modules.achievements &&
                                <ProfileAchievements />
                            }

                            {profile.modules.favourites && (
                                <ProfileFavourites />
                            )}
                        </Column>
                    </>
                )}
            </State>
        </Page>
    );
}