import Column from "@/components/layout/column";
import ChannelCard from "@/features/chat/components/channel-card";
import { useLibrary } from "@/features/library/hooks/use-library";
import type { Entry } from "@/features/library/models/entry";

interface Props {
    selected: string | null;
    onSelect: (id: string) => void;
}

export default function BotsSection({ selected, onSelect }: Props) {
    const { entries, loading, error } = useLibrary();
    if (entries.length === 0 || loading || error) return null;

    return (
        <Column>
            {entries.map((entry: Entry) => (
                <ChannelCard
                    key={entry.id}
                    username={entry.title}
                    image={entry.image}
                    description={entry.description}
                    selected={selected === entry.id}
                    onSelect={() => onSelect(entry.id)}
                />
            ))}
        </Column>
    );
}
