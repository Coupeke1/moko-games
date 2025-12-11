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
        <Section title="Achievements">
            <State data={achievements} loading={loading} error={error}/>

            {achievements && (
                <Column gap={Gap.Large}>
                    {achievements.achievements.map((a) => (
                        <AchievementCard
                            key={a.id}
                            image={a.gameImage ?? ""}
                            title={a.name}
                            description={a.description}
                            date={new Date(a.unlockedAt).toLocaleDateString()}
                            game={a.gameName ?? ""}
                        />
                    ))}
                </Column>
            )}
        </Section>
    );
}
