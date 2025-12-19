import Column from "@/components/layout/column";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import Filters from "@/features/notifications/components/filters";
import NotificationCard from "@/features/notifications/components/notification-card";
import { useNotifications } from "@/features/notifications/hooks/use-notifications";

export default function NotificationsPage() {
    const { notifications, loading, error } = useNotifications();

    return (
        <Page>
            <Filters />

            <State
                loading={loading}
                error={error}
                empty={!notifications || notifications.length === 0}
                message="No notifications"
            >
                {notifications && (
                    <Column>
                        {notifications.map((notification) => (
                            <NotificationCard
                                key={notification.id}
                                notification={notification}
                            />
                        ))}
                    </Column>
                )}
            </State>
        </Page>
    );
}
