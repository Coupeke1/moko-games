import type { Type } from "@/components/global/toast/type";
import { toast as sonnerToast, toast } from "sonner";

export default function show(type: Type, message: string) {
    return sonnerToast.custom((id) => (
        <Toast id={id} title={type.toString()} description={message} />
    ));
}

export function showNotification(title: string, message: string) {
    return sonnerToast.custom((id) => (
        <Toast id={id} title={title} description={message} />
    ));
}

interface Props {
    id: string | number;
    title: string;
    description: string;
}

function Toast({ title, description, id }: Props) {
    const onDismiss = () => toast.dismiss(id);

    return (
        <button
            className="flex font-sans flex-col min-w-72 items-start bg-bg-2 shadow-2xl p-4 rounded-lg cursor-pointer"
            onClick={onDismiss}
        >
            <h1 className="text-lg font-bold">{title}</h1>
            <p className="text-fg-2 text-left">{description}</p>
        </button>
    );
}
