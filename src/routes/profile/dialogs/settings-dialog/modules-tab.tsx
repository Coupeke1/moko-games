import Select from "@/components/inputs/select";
import Column from "@/components/layout/column";

interface Props {
    achievements: string;
    setAchievements: (achievements: string) => void;
    favourites: string;
    setFavourites: (favourites: string) => void;
}

export default function ModulesTab({
    achievements,
    setAchievements,
    favourites,
    setFavourites,
}: Props) {
    return (
        <Column>
            <Select
                label="Achievements"
                value={achievements}
                onChange={(e) => setAchievements(e.target.value)}
                options={[
                    {
                        label: "Hidden",
                        value: "hidden",
                    },
                    {
                        label: "Displayed",
                        value: "displayed",
                    },
                ]}
            />

            <Select
                label="Favourites"
                value={favourites}
                onChange={(e) => setFavourites(e.target.value)}
                options={[
                    {
                        label: "Hidden",
                        value: "hidden",
                    },
                    {
                        label: "Displayed",
                        value: "displayed",
                    },
                ]}
            />
        </Column>
    );
}
