import Row from "@/components/layout/row";
import TabLink from "@/components/tabs/links/link";

interface Props {
    tabs: { title: string, path: string }[];
}

export default function TabRow({ tabs }: Props) {
    return (
        <Row responsive={false} wrap={true}>
            {
                tabs.map((tab: { title: string; path: string }) => (
                    <TabLink key={tab.title} title={tab.title} path={tab.path} />
                ))
            }
        </Row>
    )
}