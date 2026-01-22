import { Type } from "@/features/notifications/models/type";
import { Origin } from "@/features/notifications/models/origin";
import { useSearchParams } from "react-router";
import { slug } from "@/lib/format";

export function useParams() {
    const [params, setParams] = useSearchParams();

    const type = (params.get("type") as Type | "all") || "all";
    const origin = (params.get("origin") as Origin | "all") || "all";

    const setType = async (type: Type | "all") => {
        setParams((old) => {
            const params = new URLSearchParams(old);
            if (type === "all") params.delete("type");
            else params.set("type", slug(type));
            return params;
        });
    };

    const setOrigin = (origin: Origin | "all") => {
        setParams((old) => {
            const params = new URLSearchParams(old);
            if (origin === "all") params.delete("origin");
            else params.set("origin", slug(origin));
            return params;
        });
    };

    return { type, setType, origin, setOrigin };
}
