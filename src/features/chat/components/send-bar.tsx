import Button from "@/components/buttons/button";
import Form from "@/components/inputs/form";
import Input from "@/components/inputs/input";
import { Gap } from "@/components/layout/gap";
import { useEffect, useState, type FormEvent } from "react";
import { Placeholders } from "../lib/search";

interface Props {
    onSend: (message: string) => void;
}

export default function SendBar({ onSend }: Props) {
    const [message, setMessage] = useState("");
    const [placeholder, setPlaceholder] = useState(Placeholders[0]);

    useEffect(() => {
        const index: number = Math.floor(Math.random() * Placeholders.length);
        setPlaceholder(Placeholders[index]);
    }, []);

    const handleSearch = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (message.length <= 0) return;

        onSend(message);
        setMessage("");
    };

    return (
        <Form submit={handleSearch}>
            <section className={`grid sm:grid-cols-4 ${Gap.Medium}`}>
                <section className="sm:col-span-3">
                    <Input
                        placeholder={placeholder}
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                    />
                </section>

                <Button fullWidth={true}>Send</Button>
            </section>
        </Form>
    );
}
