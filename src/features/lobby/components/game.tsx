import { Gap } from "@/components/layout/gap";
import State from "@/components/state/state";
import Page from "@/features/lobby/components/page";
import { useSession } from "@/features/lobby/hooks/use-session";
import { useRef } from "react";
import Chat from "./chat";

export default function GamePage() {
    const { lobby, profile, game, loading, error } = useSession();
    const frame = useRef<HTMLIFrameElement | null>(null);

    return (
        <Page largerWidth={true}>
            <State
                loading={loading}
                error={error}
                empty={!profile && !game}
                message="Game not found"
            >
                {profile && lobby && game && (
                    <section
                        className={`
                            grid sm:grid-cols-12
                            ${Gap.Medium}
                            md:h-[calc(100vh-12rem)]
                        `}
                    >
                        <iframe
                            ref={frame}
                            src={`${game.frontendUrl}/${lobby.startedGameId}`}
                            title={game.title}
                            className="sm:col-span-8 w-full sm:h-11/12 h-96 max-h-full rounded-lg bg-bg-3 border-3 border-bg-2"
                        />

                        <section className="sm:col-span-4 h-11/12 max-h-full w-full bg-bg-3 rounded-lg">
                            <Chat padding={true} lobby={lobby} />
                        </section>
                    </section>
                )}
            </State>
        </Page>
    );
}
