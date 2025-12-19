import { Gap } from "@/components/layout/gap";
import State from "@/components/state/state";
import Page from "@/features/game/components/page";
import { useSession } from "@/features/lobby/hooks/use-session";
import { useRef } from "react";

export default function GamePage() {
    const { lobby, profile, game, loading, error } = useSession();
    const frame = useRef(null);

    return (
        <Page>
            <State
                loading={loading}
                error={error}
                empty={!profile && !game && !profile}
                message="Game not found"
            >
                {profile && lobby && game && (
                    <section className={`grid sm:grid-cols-12 ${Gap.Medium}`}>
                        <iframe
                            ref={frame}
                            src={`${game.frontendUrl}/${lobby.startedGameId}`}
                            className="col-span-8 w-full rounded-lg h-88 bg-bg-3 border-3 border-bg-2"
                            title={game.title}
                        ></iframe>

                        <section className="col-span-4 bg-bg-3 rounded-lg flex items-center justify-center">
                            <p>Chat</p>
                        </section>
                    </section>
                )}
            </State>
        </Page>
    );
}
