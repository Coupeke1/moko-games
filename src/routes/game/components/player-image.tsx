interface Props {
    src: string;
    big?: boolean;
}

export default function PlayerImage({ src, big }: Props) {
    return (
        <section
            className={`bg-cover ${big ? "min-w-14 min-h-14" : "min-w-10 min-h-10"} rounded-full object-cover bg-fg-2`}
            style={{ backgroundImage: `url(${src})` }}
        />
    )
}