import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import ProfileAchievements from "@/features/profiles/components/achievements";
import ProfileFavourites from "@/features/profiles/components/favourites";
import ProfileInformation from "@/features/profiles/components/information";
import { useProfile } from "@/features/profiles/hooks/use-profile";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router";

export default function ProfilePage() {
    const navigate = useNavigate();
    const params = useParams();
    const name = params.name;

    useEffect(() => {
        if (!name) navigate("/profile");
    }, [name, navigate]);

    const { profile, loading, error } = useProfile(name!);

    return (
        <Page>
            <State
                loading={loading}
                error={error}
                empty={!profile}
                message="Profile not found"
            >
                {profile && (
                    <Column gap={Gap.ExtraLarge}>
                        <ProfileInformation
                            image={profile.image}
                            username={profile.username}
                            email={profile.email}
                            description={profile.description}
                            level={profile.statistics.level}
                            playTime={profile.statistics.playTime}
                        />

                        {profile.achievements && (
                            <ProfileAchievements
                                achievements={profile.achievements}
                            />
                        )}

                        {profile.favourites && (
                            <ProfileFavourites
                                favourites={profile.favourites}
                            />
                        )}
                    </Column>
                )}
            </State>
        </Page>
    );
}
