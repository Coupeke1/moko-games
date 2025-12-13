import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import TabContent from "@/components/tabs/buttons/content";
import TabRow from "@/components/tabs/buttons/row";
import showToast from "@/components/toast";
import AboutTab, {
    type AboutData,
} from "@/features/profile/dialogs/settings-dialog/about-tab";
import ModulesTab, {
    type ModulesData,
} from "@/features/profile/dialogs/settings-dialog/modules-tab";
import NotificationsTab, {
    type NotificationsData,
} from "@/features/profile/dialogs/settings-dialog/notifications-tab";
import PictureTab, {
    type PictureData,
} from "@/features/profile/dialogs/settings-dialog/picture-tab";
import type { Profile } from "@/features/profile/models/profile.ts";
import { updateProfile } from "@/features/profile/services/profile.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useRef, useState } from "react";

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

    const [current, setCurrent] = useState<string>("About");
    const aboutTab = useRef<AboutData>(null);
    const pictureTab = useRef<PictureData>(null);
    const modulesTab = useRef<ModulesData>(null);
    const notificationsTab = useRef<NotificationsData>(null);

    const save = useMutation({
        mutationFn: async () => {
            await updateProfile(
                profile.id,
                aboutTab.current?.data().description ?? profile.description,
                pictureTab.current?.data().picture ?? profile.image,
                modulesTab.current?.data() ?? profile.modules,
                notificationsTab.current?.data() ?? profile.notifications,
            );
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["profile", "me"] });

            if (profile === undefined) return;
            showToast(profile.username, "Profile was saved!");
            setCurrent("About");
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
            footer={<Button onClick={() => save.mutate()}>Save</Button>}
        >
            <Column gap={Gap.Large}>
                <TabRow
                    tabs={["About", "Picture", "Modules", "Notifications"]}
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
                                    ref={aboutTab}
                                    initial={profile.description}
                                />
                            ),
                        },
                        {
                            title: "Picture",
                            element: (
                                <PictureTab
                                    ref={pictureTab}
                                    initial={profile.image}
                                />
                            ),
                        },
                        {
                            title: "Modules",
                            element: (
                                <ModulesTab
                                    ref={modulesTab}
                                    initial={profile.modules}
                                />
                            ),
                        },
                        {
                            title: "Notifications",
                            element: (
                                <NotificationsTab
                                    ref={notificationsTab}
                                    initial={profile.notifications}
                                />
                            ),
                        },
                    ]}
                />
            </Column>
        </Dialog>
    );
}
