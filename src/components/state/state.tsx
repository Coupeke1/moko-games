import ErrorState from "@/components/state/error";
import LoadingState from "@/components/state/loading";

interface Props {
    data: unknown | undefined;
    loading: boolean;
    error: boolean;
}

export default function State({ data, loading, error }: Props) {
    if (loading) return <LoadingState />;
    if (error || !data) return <ErrorState />;
    return null;
}
