import Row from "@/components/layout/row";
import Column from "@/components/layout/column";
import {Gap} from "@/components/layout/gap";
import {Items} from "@/components/layout/items";
import {Justify} from "@/components/layout/justify";
import type {NotificationType, ReadFilter,} from "@/features/notifications/models/notification.ts";
import {NOTIFICATION_TYPES} from "@/features/notifications/models/notification.ts";

interface Props {
    read: ReadFilter;
    onReadChange: (v: ReadFilter) => void;
    type: NotificationType | "all";
    onTypeChange: (v: NotificationType | "all") => void;
}

export default function NotificationFilters({
                                                read,
                                                onReadChange,
                                                type,
                                                onTypeChange,
                                            }: Props) {
    return (
        <Row justify={Justify.Between} items={Items.Center} gap={Gap.Large}>
            <Column gap={Gap.Small}>
                <label className="text-fg-2 text-sm">Read status</label>
                <select
                    value={read}
                    onChange={(e) => onReadChange(e.target.value as ReadFilter)}
                    className="bg-bg-2 border border-bg-3 rounded-xl px-3 py-2"
                >
                    <option value="all">All</option>
                    <option value="unread">Unread</option>
                    <option value="read">Read</option>
                </select>
            </Column>

            <Column gap={Gap.Small}>
                <label className="text-fg-2 text-sm">Type</label>
                <select
                    value={type}
                    onChange={(e) =>
                        onTypeChange(e.target.value as NotificationType | "all")
                    }
                    className="bg-bg-2 border border-bg-3 rounded-xl px-3 py-2"
                >
                    <option value="all">All types</option>
                    {NOTIFICATION_TYPES.map((t) => (
                        <option key={t} value={t}>
                            {t}
                        </option>
                    ))}
                </select>
            </Column>
        </Row>
    );
}