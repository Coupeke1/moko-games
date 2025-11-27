import Row from "@/components/layout/row";
import TabLink from "@/components/tabs/buttons/link";

interface Props {
    tabs: string[];
    current: string;
    setCurrent: (tab: string) => void;
}

export default function TabRow({ tabs, current, setCurrent }: Props) {
    return (
        <Row responsive={false} wrap={true}>
            {
                tabs.map((tab: string) => (
                    <TabLink
                        key={tab}
                        title={tab}
                        active={current === tab}
                        onClick={() => setCurrent(tab)}
                    />
                ))
            }
        </Row>
    )
}