export interface Profile {
    id: string;
    username: string;
    email: string;
    description: string;
    image: string;
    statistics: {
        level: number;
        playTime: string;
    };
    modules: {
        achievements: boolean;
        favourites: boolean;
    }
}