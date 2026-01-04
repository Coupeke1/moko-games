import { showNotification } from "@/components/global/toast/toast";
import { useNotification } from "@/features/notifications/hooks/use-notification";
import type { Notification } from "@/features/notifications/models/notification";
import { useEffect, useRef } from "react";

export default function Notifications() {
    const { notifications, loading, error } = useNotification();
    const lastIndex = useRef(0);

    useEffect(() => {
        if (!notifications || loading || error) return;

        notifications
            .slice(lastIndex.current)
            .forEach((notification: Notification) => {
                showNotification(notification.title, notification.message);
            });

        lastIndex.current = notifications.length;
    }, [notifications, loading, error]);

    return null;
}
