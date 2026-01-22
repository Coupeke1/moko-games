interface Props {
    src: string;
    big?: boolean;
}

export default function Image({ src, big }: Props) {
    return (
        <section
            className={`bg-cover ${big ? "min-w-42 min-h-42" : "min-w-30 min-h-30"} rounded-lg bg-center bg-fg-2`}
            style={{ backgroundImage: `url(${src})` }}
        />
    )
}