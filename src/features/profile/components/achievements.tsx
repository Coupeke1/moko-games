import Section from "@/components/section";
import State from "@/components/state/state";
import Column from "@/components/layout/column";
import { useAchievements } from "@/features/profile/hooks/use-achievements";
import AchievementCard from "@/features/profile/components/achievement-card";
import { Gap } from "@/components/layout/gap";

export default function ProfileAchievements() {
    const { achievements, loading, error } = useAchievements();

    return (
        <Section title="Achievements">
            <State
                loading={loading}
                error={error}
                empty={achievements.length === 0}
                message="No achievements"
            >
                {achievements && (
                    <Column gap={Gap.Large}>
                        {achievements.map((achievement) => (
                            <AchievementCard
                                key={achievement.id}
                                image={
                                    achievement.gameImage ??
                                    "https://cdn2.thecatapi.com/images/28j.jpg"
                                }
                                title={achievement.name}
                                date={new Date(
                                    achievement.unlockedAt,
                                ).toLocaleDateString()}
                                game={achievement.gameName ?? "Unknown"}
                            />
                        ))}
                    </Column>
                )}
            </State>
        </Section>
    );
}
