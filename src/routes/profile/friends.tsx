import PlusIcon from "@/components/icons/plus-icon";
import Grid from "@/components/layout/grid/grid";
import Section from "@/components/section";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import { useFriends } from "@/hooks/use-friends";
import type { Friend } from "@/models/friends/friend";
import type { Profile } from "@/models/profile/profile";
import FriendCard from "@/routes/profile/components/friend-card";

interface Props {
    profile: Profile;
}

export default function FriendsSection({ profile }: Props) {
    const { friends, isLoading, isError } = useFriends();

    if (isLoading || friends === undefined) return (
        <Section title="Friends">
            <LoadingState />
        </Section>
    )

    if (isError) return (
        <Section title="Friends">
            <ErrorState />
        </Section>
    )

    return (
        <Section title="Friends">
            <Grid>
                {
                    friends.map((friend: Profile) => (
                        <FriendCard
                            image={friend.image}
                            username={friend.username}
                            level={friend.statistics.level}
                            playtime={friend.statistics.playTime.toString()}
                        />
                    ))
                }

                <button className="bg-bg-2 hover:bg-bg-3 transition-colors duration-75 flex flex-col justify-center items-center cursor-pointer select-none x-4 py-2 rounded-lg h-48">
                    <PlusIcon />
                </button>
            </Grid>
        </Section>
    )
}