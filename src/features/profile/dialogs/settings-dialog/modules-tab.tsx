import Select from "@/components/inputs/select";
import Column from "@/components/layout/column";
import type { Modules } from "@/features/profile/models/modules";
import { forwardRef, useImperativeHandle, useState } from "react";

export interface ModulesData {
    data: () => Modules;
}

const ModulesTab = forwardRef<ModulesData, { initial: Modules }>(
    ({ initial }, ref) => {
        const [achievements, setAchievements] = useState(
            initial.achievements ? "displayed" : "hidden",
        );

        const [favourites, setFavourites] = useState(
            initial.favourites ? "displayed" : "hidden",
        );

        useImperativeHandle(ref, () => ({
            data: () =>
                ({
                    achievements: achievements === "displayed",
                    favourites: favourites === "displayed",
                }) as Modules,
        }));

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
    },
);

export default ModulesTab;
