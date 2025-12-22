import { showNotification } from "@/components/global/toast/toast";
import { useNotification } from "@/features/notifications/hooks/use-notification";
import type { Notification } from "@/features/notifications/models/notification";

export default function Notifications() {
    const { notifications, loading, error } = useNotification();

    if (!notifications || loading || error) return null;
    if (notifications.length <= 0) return null;

    notifications.forEach((notification: Notification) => {
        showNotification(notification.title, notification.message);
    });
}
