import Page from "@/components/layout/page";
import Column from "@/components/layout/column";
import {Gap} from "@/components/layout/gap";
import State from "@/components/state/state";
import {useState} from "react";
import NotificationFilters from "@/features/notifications/components/notification-filters";
import NotificationCard from "@/features/notifications/components/notification-card";
import {type NotificationType, type ReadFilter,} from "@/features/notifications/models/notification";
import {useNotifications} from "@/features/notifications/hooks/use-notification";

export default function NotificationPage() {
    const [read, setRead] = useState<ReadFilter>("all");
    const [type, setType] = useState<NotificationType | "all">("all");

    const {notifications, loading, error, markAsRead} = useNotifications({
        read,
        type,
    });

    return (
        <Page>
            <State data={notifications} loading={loading} error={error}/>

            {notifications && (
                <Column gap={Gap.Large}>
                    <NotificationFilters
                        read={read}
                        onReadChange={setRead}
                        type={type}
                        onTypeChange={setType}
                    />

                    <Column>
                        {notifications.length === 0 && (
                            <p className="text-fg-2">No notifications.</p>
                        )}

                        {notifications.map((n) => (
                            <NotificationCard
                                key={n.id}
                                notification={n}
                                onMarkRead={markAsRead}
                            />
                        ))}
                    </Column>
                </Column>
            )}
        </Page>
    );
}