import Message from "@/components/state/message";

export default function LoadingState() {
    return (
        <main className="bg-bg my-14 flex items-center justify-center">
            <Message pulsing={true}>Loading...</Message>
        </main>
    )
}