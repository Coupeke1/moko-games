import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import Page from "@/components/layout/page";
import Message from "@/components/state/message";
import State from "@/components/state/state";
import { useVerify } from "@/features/checkout/hooks/use-verify";
import { useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router";

export default function OrderPage() {
    const client = useQueryClient();
    const navigate = useNavigate();
    const params = useParams();
    const id = params.id;

    useEffect(() => {
        if (!id) navigate("/checkout");
    }, [id, navigate]);

    const { order, loading, error } = useVerify(id);

    useEffect(() => {
        if (error) show(Type.Order, "Could not verify");

        if (order) {
            client.invalidateQueries({ queryKey: ["library"] });
            navigate("/library");
        }
    }, [order, error, client, navigate]);

    return (
        <Page>
            <State
                loading={loading || loading}
                error={error || error}
                message="No order"
            >
                <Message>Redirecting</Message>
            </State>
        </Page>
    );
}
