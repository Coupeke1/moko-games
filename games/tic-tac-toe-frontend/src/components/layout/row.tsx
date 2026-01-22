import type {ReactNode} from "react";

export default function Row ({ label, value }: { label: string; value: ReactNode }) {
    return (
        <div className="status-message text-fg-2 text-lg">
            {label}: <span className="text-fg font-bold">{value}</span>
        </div>
    );
}