import Page from "@/components/layout/page";
import State from "@/components/state/state";
import showToast from "@/components/toast";
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
        if (error) showToast("Order", "Could not verify order");

        if (order) {
            showToast("Order", "Sucessful");
            client.invalidateQueries({ queryKey: ["library"] });
            navigate("/library");
        }
    }, [order, error, client, navigate]);

    return (
        <Page>
            <State
                data={order}
                loading={loading || loading}
                error={error || error}
            />
        </Page>
    );
}
