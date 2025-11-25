export default function Image({ src }: { src: string }) {
    return (
        <section
            className="bg-cover min-w-30 min-h-30 rounded-lg bg-center bg-fg-2"
            style={{ backgroundImage: `url(${src})` }}
        />
    )
}