import Message from "@/components/state/message";
import type { ReactNode } from "react";

interface Props {
    children?: ReactNode;
}

export default function ErrorState({ children }: Props) {
    return (
        <main className="bg-bg my-2 flex items-center justify-center">
            <Message>
                {children ? children + " :(" : "Something went wrong :("}
            </Message>
        </main>
    );
}
