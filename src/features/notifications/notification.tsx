import Page from "@/components/layout/page";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import State from "@/components/state/state";
import { useState } from "react";
import NotificationFilters from "@/features/notifications/components/notification-filters";
import NotificationCard from "@/features/notifications/components/notification-card";
import {
    type Notification,
    type ReadFilter,
} from "@/features/notifications/models/notification";
import { useNotifications } from "@/features/notifications/hooks/use-notification";
import ErrorState from "@/components/state/error";
import type { Origin } from "@/features/notifications/models/type";

export default function NotificationPage() {
    const [status, setStatus] = useState<ReadFilter>("all");
    const [type, setType] = useState<Origin | "all">("all");

    const { notifications, loading, error, markAsRead } = useNotifications({
        status,
        type,
    });

    return (
        <Page>
            <NotificationFilters
                status={status}
                onStatusChange={setStatus}
                type={type}
                onTypeChange={setType}
            />

            <State data={notifications} loading={loading} error={error} />

            {notifications && (
                <Column gap={Gap.Large}>
                    <Column>
                        {notifications.length === 0 && (
                            <ErrorState>No notifications</ErrorState>
                        )}
                        {notifications.map((notification: Notification) => (
                            <NotificationCard
                                key={notification.id}
                                notification={notification}
                                onMarkRead={markAsRead}
                            />
                        ))}
                    </Column>
                </Column>
            )}
        </Page>
    );
}
