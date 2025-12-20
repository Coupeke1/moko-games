import { Gap } from "@/components/layout/gap";
import State from "@/components/state/state";
import Page from "@/features/game/components/page";
import { useSession } from "@/features/lobby/hooks/use-session";
import { useRef } from "react";

export default function GamePage() {
    const { lobby, profile, game, loading, error } = useSession();
    const frame = useRef<HTMLIFrameElement | null>(null);

    return (
        <Page>
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
                            h-[calc(100vh-4rem)]
                        `}
                    >
                        <iframe
                            ref={frame}
                            src={`${game.frontendUrl}/${lobby.startedGameId}`}
                            title={game.title}
                            className="col-span-8 w-full h-11/12 max-h-full rounded-lg bg-bg-3 border-3 border-bg-2"
                        />

                        <section className="col-span-4 h-11/12 max-h-full bg-bg-3 rounded-lg flex items-center justify-center">
                            <p>Chat</p>
                        </section>
                    </section>
                )}
            </State>
        </Page>
    );
}
