import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Section from "@/components/section";
import State from "@/components/state/state";
import type { Game } from "@/features/games/models/game";
import { useAchievements } from "@/features/library/hooks/use-achievements";
import AchievementCard from "@/features/profiles/components/achievement-card";
import type { Achievement } from "@/features/profiles/models/achievement";

interface Props {
    game: Game;
}

export default function GameAchievements({ game }: Props) {
    const { achievements, loading, error } = useAchievements(game.id);

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
