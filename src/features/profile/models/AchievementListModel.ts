export interface AchievementModel {
    id: string;
    gameId: string;
    code: string;
    name: string;
    description: string;
    level: number;
    unlockedAt: string;
    gameName: string | null;
    gameImage: string | null;
}

export interface AchievementListModel {
    achievements: AchievementModel[];
}