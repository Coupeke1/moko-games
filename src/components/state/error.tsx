import Message from "@/components/state/message";

export default function ErrorState() {
    return (
        <main className="bg-bg my-14 flex items-center justify-center">
            <Message>Something went wrong :(</Message>
        </main>
    )
}