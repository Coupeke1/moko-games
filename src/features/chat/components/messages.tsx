import State from "@/components/state/state";
import { useMessage } from "@/features/chat/hooks/use-message";
import type { Channel } from "@/features/chat/models/channel";

interface Props {
    channel: Channel;
}

export default function MessagesSection({ channel }: Props) {
    const { message, loading, error } = useMessage(channel.id);

    return (
        <State
            loading={loading}
            error={error}
            empty={!message}
            message="No message"
        >
            {message && <p>{message.content}</p>}
        </State>
    );
}
