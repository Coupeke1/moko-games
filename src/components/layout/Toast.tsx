import {useEffect} from "react";

interface Props {
    message: string;
    onClose: () => void;
    duration?: number; // ms
}

export default function Toast({ message, onClose, duration = 4000 }: Props) {
    useEffect(() => {
        if (!duration) return;
        const t = setTimeout(onClose, duration);
        return () => clearTimeout(t);
    }, [duration, onClose]);

    return (
        <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50
                    bg-red-500 text-white px-4 py-2 rounded-lg shadow-lg
                    flex items-center gap-3 animate-fade-in">
            <span>{message}</span>
            <button onClick={onClose} className="ml-2 text-sm font-bold">✕</button>
        </div>
    );
}