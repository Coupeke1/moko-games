import Row from "@/components/layout/row";
import TabLink from "@/routes/friends/components/tabs/link";

export default function TabRow() {
    return (
        <Row responsive={false} wrap={true}>
            <TabLink title="Friends" path="/friends" />
            <TabLink title="Incoming" path="/friends/requests/incoming" />
            <TabLink title="Outgoing" path="/friends/requests/outgoing" />
        </Row>
    )
}