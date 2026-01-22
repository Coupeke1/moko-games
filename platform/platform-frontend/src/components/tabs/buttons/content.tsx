import type { Tab } from "@/components/tabs/buttons/tab";

interface Props {
    tabs: Tab[];
    current: string;
}

export default function TabContent({ tabs, current }: Props) {
    const currentTab: Tab | undefined = tabs.find((tab: Tab) => tab.title === current);
    return currentTab ? currentTab.element : null;
}