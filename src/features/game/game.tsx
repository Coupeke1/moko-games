import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import State from "@/components/state/state";
import Page from "@/features/lobby/components/page";
import { useSession } from "@/features/lobby/hooks/use-session";
import { useRef } from "react";

export default function GamePage() {
    const { lobby, profile, game, loading, error } = useSession();
    const frame = useRef(null);

    return (
        <Page>
            <State
                data={profile && lobby && game}
                loading={loading}
                error={error}
            />

            {profile && lobby && game && (
                <Column gap={Gap.ExtraLarge}>
                    <iframe
                        ref={frame}
                        src={`${game.frontendUrl}/${lobby.startedGameId}`}
                        className="rounded-lg h-88 bg-bg-3 border-3 border-bg-2"
                        title={game.title}
                    ></iframe>
                </Column>
            )}
        </Page>
    );
}
