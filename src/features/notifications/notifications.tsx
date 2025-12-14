import Column from "@/components/layout/column";
import Page from "@/components/layout/page";
import State from "@/components/state/state";
import Filters from "@/features/notifications/components/filters";
import NotificationCard from "@/features/notifications/components/notification-card";
import { useNotifications } from "@/features/notifications/hooks/use-notifications";
import { useEffect, useRef } from "react";

export default function NotificationsPage() {
    const {
        notifications,
        loading,
        fetching,
        error,
        fetchNextPage,
        hasNextPage,
    } = useNotifications();

    const observerTarget = useRef<HTMLElement>(null);

    useEffect(() => {
        const observer = new IntersectionObserver(
            ([entry]) => {
                if (!entry.isIntersecting || !hasNextPage || fetching) return;
                fetchNextPage();
            },
            { rootMargin: "200px", threshold: 0 },
        );

        const currentTarget = observerTarget.current;
        if (currentTarget) observer.observe(currentTarget);

        return () => {
            if (currentTarget) observer.unobserve(currentTarget);
        };
    }, [fetchNextPage, hasNextPage, fetching]);

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
                                key={notification.data.id}
                                notification={notification.data}
                            />
                        ))}

                        <section ref={observerTarget} className="h-4" />
                    </Column>
                )}
            </State>
        </Page>
    );
}
