import Message from "@/components/state/message";

export default function LoadingState() {
    return (
        <main className="my-2 flex items-center justify-center">
            <Message pulsing={true}>Loading...</Message>
        </main>
    );
}
