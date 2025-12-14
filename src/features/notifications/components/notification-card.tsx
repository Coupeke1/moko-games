import Card from "@/components/cards/card";
import CalendarIcon from "@/components/icons/calendar-icon";
import ReadIcon from "@/components/icons/read-icon";
import UnreadIcon from "@/components/icons/unread-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { Height } from "@/components/layout/size";
import type { Notification } from "@/features/notifications/models/notification.ts";
import { date } from "@/lib/format";

interface Props {
    notification: Notification;
}

export default function NotificationCard({ notification }: Props) {
    return (
        <Card
            height={Height.Small}
            title={notification.title}
            information={
                <Row gap={Gap.Small} items={Items.Center} responsive={false}>
                    <CalendarIcon />
                    {date(notification.createdAt)}
                </Row>
            }
            options={
                <button className="cursor-pointer text-fg-2 hover:text-fg transition-colors duration-75">
                    {notification.read ? <ReadIcon /> : <UnreadIcon />}
                </button>
            }
        />
    );
}
