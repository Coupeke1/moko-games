import Column from "@/components/layout/column";
import Page from "@/components/layout/page";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import Filters from "@/features/notifications/components/filters";
import NotificationCard from "@/features/notifications/components/notification-card";
import { useNotifications } from "@/features/notifications/hooks/use-notifications";
import { type Notification } from "@/features/notifications/models/notification";
import { useQueryClient } from "@tanstack/react-query";

export default function NotificationsPage() {
    const client = useQueryClient();
    const { notifications, loading, error } = useNotifications();

    const search = (type: string, origin: string) => {
        client.setQueryData(["notifications", "params"], { type, origin });
        client.invalidateQueries({ queryKey: ["notifications"] });
    };

    return (
        <Page>
            <Filters onSearch={search} />
            <State data={notifications} loading={loading} error={error} />

            {notifications &&
                (notifications.items.length == 0 ? (
                    <ErrorState>No notifications</ErrorState>
                ) : (
                    <Column>
                        {notifications.items.map(
                            (notification: Notification) => (
                                <NotificationCard
                                    key={notification.id}
                                    notification={notification}
                                />
                            ),
                        )}
                    </Column>
                ))}
        </Page>
    );
}
