import Page from "@/routes/lobby/components/page";
import { useParams } from "react-router";

export default function LobbyPage() {
    const { id } = useParams();

    return (
        <Page>
            <p>Lobby {id}</p>
        </Page>
    )
}