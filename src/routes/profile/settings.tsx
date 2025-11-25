import Button from "@/components/buttons/button";
import BackIcon from "@/components/icons/back-icon";
import InputBox from "@/components/inputs/box";
import Input from "@/components/inputs/input";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import Page from "@/components/layout/page";
import Stack from "@/components/layout/stack";
import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import showToast from "@/components/toast";
import { useMyProfile } from "@/hooks/use-my-profile";
import type { Profile } from "@/models/profile";
import Image from "@/routes/profile/components/image";
import { updateProfile } from "@/services/profile-service";
import { useAuthStore } from "@/stores/auth-store";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { Link as RouterLink, useNavigate } from "react-router";

export default function ProfileSettingsPage() {
    const client = useQueryClient();
    const navigate = useNavigate();

    const { token } = useAuthStore();
    const { profile, isLoading, isError } = useMyProfile();

    const [image, setImage] = useState(profile ? profile.image : "");
    const [description, setDescription] = useState(profile ? profile.description : "");

    const save = useMutation({
        mutationFn: async ({ profile, image, description }: { profile: Profile, image: string, description: string }) => {
            await updateProfile(profile.id, description, image);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["profile", "me", token] });

            if (profile === undefined) return;
            showToast(profile.username, "Profile was saved!");
            navigate("/profile");
        },
        onError: (error: Error) => {
            if (profile === undefined) return;
            showToast(profile.username, error.message);
        }
    });

    function handleSave() {
        if (profile === undefined || isLoading || isError) return;
        save.mutate({ profile, description, image });
    };

    useEffect(() => {
        setImage(profile ? profile.image : "");
        setDescription(profile ? profile.description : "");
    }, [profile]);


    if (isLoading || profile === undefined) return <Page><LoadingState /></Page>
    if (isError) return <Page><ErrorState /></Page>

    return (
        <Page>
            <Column gap={Gap.ExtraLarge}>
                <Stack items={Items.Start}>
                    <RouterLink to="/profile" className={`flex flex-row ${Gap.Medium} ${Items.Center} ${Justify.Center} text-fg-2 hover:text-fg transition-colors duration-75`}>
                        <BackIcon />
                        <span className="text-xl">Profile</span>
                    </RouterLink>
                </Stack>

                <Column>
                    <Column items={Items.Start}>
                        <Image src={image} />
                    </Column>

                    <Input label="Picture" value={image} onChange={(e) => setImage(e.target.value)} />
                    <InputBox label="About" value={description} onChange={(e) => setDescription(e.target.value)} />
                </Column>

                <Button onClick={() => handleSave()} disabled={save.isPending}>Save</Button>
            </Column>
        </Page>
    )
}