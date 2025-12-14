import Column from "@/components/layout/column";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import Filters from "@/features/notifications/components/filters";
import NotificationCard from "@/features/notifications/components/notification-card";
import { useNotifications } from "@/features/notifications/hooks/use-notifications";

export default function NotificationsPage() {
    const {
        notifications,
        loading,
        fetching,
        error,
        fetchNextPage,
        hasNextPage,
    } = useNotifications();

    return (
        <Page>
            <Filters />

            <State
                loading={loading}
                error={error}
                empty={notifications.length === 0}
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

                        {hasNextPage && (
                            <button
                                onClick={() => fetchNextPage()}
                                disabled={fetching}
                                className="mt-4 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
                            >
                                {fetching ? "Loading..." : "Load More"}
                            </button>
                        )}
                    </Column>
                )}
            </State>
        </Page>
    );
}
