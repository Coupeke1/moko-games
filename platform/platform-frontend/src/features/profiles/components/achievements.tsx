import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Section from "@/components/section";
import State from "@/components/state/state";
import AchievementCard from "@/features/profiles/components/achievement-card";
import type { Achievement } from "@/features/profiles/models/achievement";

interface Props {
    achievements: Achievement[];
    loading?: boolean;
    error?: boolean;
}

export default function ProfileAchievements({
    achievements,
    loading,
    error,
}: Props) {
    return (
        <Section title="Achievements">
            <State
                loading={loading ?? false}
                error={error ?? false}
                empty={achievements.length === 0}
                message="No achievements"
            >
                {achievements && (
                    <Column gap={Gap.Large}>
                        {achievements.map((achievement: Achievement) => (
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
