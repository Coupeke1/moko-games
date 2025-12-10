interface Props {
    src?: string | null;
    big?: boolean;
}

export default function PlayerImage({ src, big }: Props) {
    const size = big ? "min-w-20 min-h-20" : "min-w-10 min-h-10";

    if (!src) {
        return (
            <div className={`${size} rounded-full bg-fg-2/30 flex items-center justify-center`}>
                <span className="text-fg-2">👤</span>
            </div>
        );
    }

    return (
        <section
            className={`bg-cover ${size} rounded-full object-cover bg-fg-2`}
            style={{ backgroundImage: `url(${src})` }}
        />
    )
}