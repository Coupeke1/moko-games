import Section from "@/components/section";
import State from "@/components/state/state";
import Column from "@/components/layout/column";
import {useAchievements} from "@/features/profile/hooks/use-achievements";
import AchievementCard from "@/features/profile/components/achievement-card";
import {Gap} from "@/components/layout/gap";

interface Props {
    enabled: boolean;
}

export default function AchievementSection({enabled}: Props) {
    const {achievements, loading, error} = useAchievements();

    if (!enabled) return null;
    return (
        <Section title="achievements">
            <State data={achievements} loading={loading} error={error}/>

            {achievements && (
                <Column gap={Gap.Large}>
                    {achievements.map((achievement) => (
                        <AchievementCard
                            key={achievement.id}
                            image={achievement.gameImage ?? ""}
                            title={achievement.name}
                            description={achievement.description}
                            date={new Date(achievement.unlockedAt).toLocaleDateString()}
                            game={achievement.gameName ?? ""}
                        />

                    ))}
                </Column>

            )}
        </Section>
    );
}


