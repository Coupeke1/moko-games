import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";
import { type ReactNode } from "react";

interface Props {
    loading: boolean;
    error: boolean;
    empty?: boolean;
    message: string;
    children: ReactNode;
}

export default function State({
    loading,
    error,
    empty = false,
    message,
    children,
}: Props) {
    if (loading) return <LoadingState />;
    if (error) return <ErrorState />;
    if (empty) return <ErrorState>{message}</ErrorState>;
    return <>{children}</>;
}
