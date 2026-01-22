interface Props {
    src?: string;
    big?: boolean;
    loading?: boolean,
}

export default function Image({ src, big, loading = false }: Props) {
    if (loading || !src) return (
        <section
            className={`bg-cover ${big ? "min-w-42 min-h-42" : "min-w-20 min-h-20"} rounded-lg bg-center bg-fg-2 animate-pulse`}
        />
    )

    return (
        <section
            className={`bg-cover ${big ? "min-w-42 min-h-42" : "min-w-20 min-h-20"} rounded-lg bg-center bg-fg-2`}
            style={{ backgroundImage: `url(${src})` }}
        />
    )
}