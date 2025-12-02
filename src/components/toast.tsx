import { toast as sonnerToast, toast } from "sonner";

export default function showToast(title: string, description: string) {
    return sonnerToast.custom((id) =>  (
        <Toast
            id={id}
            title={title}
            description={description}
        />
    ))
}

interface Props {
    id: string | number;
    title: string;
    description: string;
}

function Toast({ title, description, id }: Props) {
    const onDismiss = () => {
        toast.dismiss(id);
    };

    return (
        <button className="flex font-sans flex-col min-w-72 items-start bg-bg-2 shadow-2xl p-4 rounded-lg cursor-pointer" onClick={onDismiss}>
            <h1 className="text-lg font-bold">{title}</h1>
            <p className="text-fg-2 text-left">{description}</p>
        </button>
    );
}