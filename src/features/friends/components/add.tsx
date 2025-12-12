import Button from "@/components/buttons/button";
import Form from "@/components/inputs/form";
import Input from "@/components/inputs/input";
import { Gap } from "@/components/layout/gap";
import showToast from "@/components/toast";
import { sendRequest } from "@/features/friends/services/requests.ts";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState, type FormEvent } from "react";

export default function Add() {
    const client = useQueryClient();
    const [username, setUsername] = useState("");

    const add = useMutation({
        mutationFn: async ({ username }: { username: string }) =>
            await sendRequest(username),
        onSuccess: async () => {
            await client.refetchQueries({ queryKey: ["friends"] });
            setUsername("");
            showToast(username, "Request sent");
        },
        onError: (error: Error) => {
            showToast("Friends", error.message);
        },
    });

    const handleAdd = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        add.mutate({ username });
    };

    return (
        <Form submit={handleAdd}>
            <section
                onSubmit={handleAdd}
                className={`grid sm:grid-cols-4 ${Gap.Medium}`}
            >
                <section className="sm:col-span-3">
                    <Input
                        placeholder="Search Username..."
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </section>

                <Button>Add</Button>
            </section>
        </Form>
    );
}
