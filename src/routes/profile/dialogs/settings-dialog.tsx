import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import InputBox from "@/components/inputs/box";
import Input from "@/components/inputs/input";
import Select from "@/components/inputs/select";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import TabContent from "@/components/tabs/content";
import TabRow from "@/components/tabs/row";
import showToast from "@/components/toast";
import type { Modules } from "@/models/profile/modules";
import type { Profile } from "@/models/profile/profile";
import { updateProfile } from "@/services/profile-service";
import { useAuthStore } from "@/stores/auth-store";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import Image from "@/routes/profile/components/image";

interface Props {
    profile: Profile;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function SettingsDialog({ profile, close, open, onChange }: Props) {
    const client = useQueryClient();
    const { token } = useAuthStore();

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
    }, [profile, open]);

    const save = useMutation({
        mutationFn: async (
            { profile, description, image, achievements, favourites }: {
                profile: Profile,
                description: string,
                image: string,
                achievements: boolean,
                favourites: boolean
            }
        ) => {
            await updateProfile(profile.id, description, image, { achievements, favourites } as Modules);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["profile", "me", token] });

            if (profile === undefined) return;
            showToast(profile.username, "Profile was saved!");
            close();
        },
        onError: (error: Error) => {
            if (profile === undefined) return;
            showToast(profile.username, error.message);
        }
    });

    function handleSave() {
        save.mutate({
            profile,
            description,
            image,
            achievements: (achievements === "displayed"),
            favourites: (favourites === "displayed")
        });
    };

    return (
        <Dialog title="Profile Settings" open={open} onChange={onChange} footer={
            <Button onClick={handleSave}>Save</Button>
        }>
            <Column gap={Gap.Large}>
                <TabRow
                    tabs={["About", "Picture", "Modules"]}
                    current={current}
                    setCurrent={setCurrent}
                />

                <TabContent
                    current={current}
                    tabs={[{
                        title: "About", element: (
                            <Column>
                                <InputBox label="About" value={description} onChange={(e) => { setDescription(e.target.value) }} />
                            </Column>
                        )
                    },
                    {
                        title: "Picture", element: (
                            <Column>
                                <Input label="Picture" value={image} onChange={(e) => setImage(e.target.value)} />
                                <Image src={image} />
                            </Column>
                        )
                    },
                    {
                        title: "Modules", element: (
                            <Column>
                                <Select label="Achievements" value={achievements} onChange={(e) => setAchievements(e.target.value)} options={[
                                    { label: "Hidden", value: "hidden" },
                                    { label: "Displayed", value: "displayed" }
                                ]} />

                                <Select label="Favourites" value={favourites} onChange={(e) => setFavourites(e.target.value)} options={[
                                    { label: "Hidden", value: "hidden" },
                                    { label: "Displayed", value: "displayed" }
                                ]} />
                            </Column>
                        )
                    }]}
                />
            </Column>
        </Dialog>
    );
}