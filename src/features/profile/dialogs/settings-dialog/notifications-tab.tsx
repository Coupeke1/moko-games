import Select from "@/components/inputs/select";
import Column from "@/components/layout/column";
import type { Notifications } from "@/features/profile/models/notifications";
import { forwardRef, useImperativeHandle, useState } from "react";

export interface NotificationsData {
    data: () => Notifications;
}

const NotificationsTab = forwardRef<
    NotificationsData,
    { initial: Notifications }
>(({ initial }, ref) => {
    const [email, setEmail] = useState(
        initial.receiveEmail ? "enabled" : "disabled",
    );
    const [social, setSocial] = useState(
        initial.social ? "enabled" : "disabled",
    );
    const [achievements, setAchievements] = useState(
        initial.achievements ? "enabled" : "disabled",
    );
    const [commerce, setCommerce] = useState(
        initial.commerce ? "enabled" : "disabled",
    );
    const [chat, setChat] = useState(initial.chat ? "enabled" : "disabled");

    useImperativeHandle(
        ref,
        () => ({
            data: () =>
                ({
                    receiveEmail: email === "enabled",
                    social: social === "enabled",
                    achievements: achievements === "enabled",
                    commerce: commerce === "enabled",
                    chat: chat === "enabled",
                }) as Notifications,
        }),
        [email, social, achievements, commerce, chat],
    );

    return (
        <Column>
            <Select
                label="Emails"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                options={[
                    {
                        label: "Disabled",
                        value: "disabled",
                    },
                    {
                        label: "Enabled",
                        value: "enabled",
                    },
                ]}
            />

            <Select
                label="Social"
                value={social}
                onChange={(e) => setSocial(e.target.value)}
                options={[
                    {
                        label: "Disabled",
                        value: "disabled",
                    },
                    {
                        label: "Enabled",
                        value: "enabled",
                    },
                ]}
            />

            <Select
                label="Achievements"
                value={achievements}
                onChange={(e) => setAchievements(e.target.value)}
                options={[
                    {
                        label: "Disabled",
                        value: "disabled",
                    },
                    {
                        label: "Enabled",
                        value: "enabled",
                    },
                ]}
            />

            <Select
                label="Store"
                value={commerce}
                onChange={(e) => setCommerce(e.target.value)}
                options={[
                    {
                        label: "Disabled",
                        value: "disabled",
                    },
                    {
                        label: "Enabled",
                        value: "enabled",
                    },
                ]}
            />

            <Select
                label="Chat"
                value={chat}
                onChange={(e) => setChat(e.target.value)}
                options={[
                    {
                        label: "Disabled",
                        value: "disabled",
                    },
                    {
                        label: "Enabled",
                        value: "enabled",
                    },
                ]}
            />
        </Column>
    );
});

export default NotificationsTab;
