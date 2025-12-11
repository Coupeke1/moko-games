import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import {Gap} from "@/components/layout/gap";
import TabContent from "@/components/tabs/buttons/content";
import TabRow from "@/components/tabs/buttons/row";
import showToast from "@/components/toast";
import type {Modules} from "@/features/profile/models/modules.ts";
import type {Profile} from "@/features/profile/models/profile.ts";
import {updateProfile} from "@/features/profile/services/profile.ts";
import {useAuthStore} from "@/stores/auth-store";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {useEffect, useState} from "react";
import AboutTab from "@/features/profile/dialogs/settings-dialog/about-tab";
import ModulesTab from "@/features/profile/dialogs/settings-dialog/modules-tab";
import PictureTab from "@/features/profile/dialogs/settings-dialog/picture-tab";

interface Props {
    profile: Profile;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function SettingsDialog({
                                           profile,
                                           close,
                                           open,
                                           onChange,
                                       }: Props) {
    const client = useQueryClient();
    const {token} = useAuthStore();

    const [current, setCurrent] = useState<string>("About");
    const [image, setImage] = useState("");
    const [description, setDescription] = useState("");
    const [achievements, setAchievements] = useState("hidden");
    const [favourites, setFavourites] = useState("hidden");

    useEffect(() => {
        setDescription(profile.description);
        setImage(profile.image);
        setAchievements(profile.modules.achievements ? "displayed" : "hidden");
        setFavourites(profile.modules.favourites ? "displayed" : "hidden");
    }, [open, profile.description, profile.image, profile.modules.achievements, profile.modules.favourites]);

    const save = useMutation({
        mutationFn: async ({
                               profile,
                               description,
                               image,
                               achievements,
                               favourites,
                           }: {
            profile: string;
            description: string;
            image: string;
            achievements: boolean;
            favourites: boolean;
        }) => {
            await updateProfile(profile, description, image, {
                achievements,
                favourites,
            } as Modules);
        },
        onSuccess: async () => {
            await client.refetchQueries({queryKey: ["profile", "me", token]});

            if (profile === undefined) return;
            showToast(profile.username, "Profile was saved!");
            close();
        },
        onError: (error: Error) => {
            if (profile === undefined) return;
            showToast(profile.username, error.message);
        },
    });

    return (
        <Dialog
            title="Profile Settings"
            onClose={() => setCurrent("About")}
            open={open}
            onChange={onChange}
            footer={
                <Button
                    onClick={() =>
                        save.mutate({
                            profile: profile.id,
                            description,
                            image,
                            achievements: achievements === "displayed",
                            favourites: favourites === "displayed",
                        })
                    }
                >
                    Save
                </Button>
            }
        >
            <Column gap={Gap.Large}>
                <TabRow
                    tabs={["About", "Picture", "Modules"]}
                    current={current}
                    setCurrent={setCurrent}
                />

                <TabContent
                    current={current}
                    tabs={[
                        {
                            title: "About",
                            element: (
                                <AboutTab
                                    description={description}
                                    setDescription={setDescription}
                                />
                            ),
                        },
                        {
                            title: "Picture",
                            element: (
                                <PictureTab image={image} setImage={setImage}/>
                            ),
                        },
                        {
                            title: "Modules",
                            element: (
                                <ModulesTab
                                    achievements={achievements}
                                    setAchievements={setAchievements}
                                    favourites={favourites}
                                    setFavourites={setFavourites}
                                />
                            ),
                        },
                    ]}
                />
            </Column>
        </Dialog>
    );
}
