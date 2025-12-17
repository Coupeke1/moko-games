import Card from "@/components/cards/card";
import CalendarIcon from "@/components/icons/calendar-icon";
import CategoryIcon from "@/components/icons/category-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { Height } from "@/components/layout/size";
import Stack from "@/components/layout/stack";
import showToast from "@/components/global/toast";
import type { Notification } from "@/features/notifications/models/notification.ts";
import {
    getRoute,
    readNotification,
} from "@/features/notifications/services/notifications";
import { format, relativeDate } from "@/lib/format";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router";

interface Props {
    notification: Notification;
}

export default function NotificationCard({ notification }: Props) {
    const navigate = useNavigate();
    const client = useQueryClient();

    const read = useMutation({
        mutationFn: async () => {
            if (!notification.read) await readNotification(notification.id);

            const route: string | null = getRoute(notification);
            if (route === null) return;

            navigate(route);
        },
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["notifications"] });
        },
        onError: (error: Error) => showToast(notification.title, error.message),
    });

    return (
        <Card
            height={Height.Small}
            onClick={() => read.mutate()}
            title={notification.title}
            information={
                <section className="min-w-0">
                    <Stack>
                        <p className="truncate text-fg-2">
                            {notification.message}
                        </p>

                        <Row responsive={false}>
                            <Row
                                gap={Gap.Small}
                                items={Items.Center}
                                responsive={false}
                            >
                                <CalendarIcon />
                                {relativeDate(notification.createdAt)}
                            </Row>

                            <Row
                                gap={Gap.Small}
                                items={Items.Center}
                                responsive={false}
                            >
                                <CategoryIcon />
                                {format(notification.origin)}
                            </Row>
                        </Row>
                    </Stack>
                </section>
            }
        />
    );
}
