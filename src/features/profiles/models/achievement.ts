export interface Achievement {
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