interface Props {
    username: string;
    image: string;
    timestamp: string;
    selected: boolean;
}

export default function ChannelCard({
    username,
    image,
    timestamp,
    selected,
}: Props) {
    return (
        <article
            className={`p-2 w-full rounded-lg flex items-center gap-2 ${selected ? "bg-bg-3" : "hover:bg-bg-3 transition-colors duration-75"} cursor-pointer select-none`}
        >
            <section
                className={`bg-cover min-w-14 min-h-14 rounded-lg bg-center bg-fg-2`}
                style={{
                    backgroundImage: `url("${image}")`,
                }}
            />
            <section className="flex flex-col min-w-0">
                <p className="font-bold text-lg truncate">{username}</p>
                <p className="text-fg-2">{timestamp}</p>
            </section>
        </article>
    );
}
