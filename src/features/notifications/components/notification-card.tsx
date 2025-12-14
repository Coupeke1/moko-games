import Row from "@/components/layout/row";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import type { Notification } from "@/features/notifications/models/notification.ts";

function formatDate(iso: string) {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return iso;
    return d.toLocaleString();
}

interface Props {
    notification: Notification;
}
export default function NotificationCard({ notification }: Props) {
    return (
        <button
            className={[
                "w-full text-left rounded-2xl border px-4 py-3 transition-colors",
                notification.read
                    ? "border-bg-3 bg-bg-2"
                    : "border-accent/30 bg-accent/10 hover:bg-accent/15",
            ].join(" ")}
        >
            <Row justify={Justify.Between} items={Items.Start} gap={Gap.Large}>
                <Column gap={Gap.Small}>
                    <Row
                        items={Items.Center}
                        gap={Gap.Small}
                        responsive={false}
                    >
                        <span
                            className={[
                                "text-xs px-2 py-1 rounded-full border",
                                notification.read
                                    ? "border-bg-3 text-fg-2"
                                    : "border-accent/40 text-accent",
                            ].join(" ")}
                        >
                            {notification.read ? "Read" : "Unread"}
                        </span>

                        <span className="text-xs px-2 py-1 rounded-full border border-bg-3 text-fg-2">
                            Label
                        </span>
                    </Row>

                    <h3 className="text-lg font-semibold">
                        {notification.title}
                    </h3>
                    <p className="text-fg-2">{notification.message}</p>
                </Column>

                <span className="text-xs text-fg-2 whitespace-nowrap">
                    {formatDate(notification.createdAt)}
                </span>
            </Row>
        </button>
    );
}
