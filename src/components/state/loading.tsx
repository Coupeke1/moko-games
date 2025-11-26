export default function LoadingState() {
    return (
        <div className="flex-grow flex items-center justify-center">
            <div className="flex flex-col items-center gap-4">
                <span className="w-10 h-10 block border-3 border-fg-2/30 border-t-fg animate-spin rounded-full" />
                <p className="text-fg-2 animate-pulse">Loading…</p>
            </div>
        </div>
    );
}