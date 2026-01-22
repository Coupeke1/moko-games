import Grid from "@/components/layout/grid/grid";
import Section from "@/components/section";
import State from "@/components/state/state";
import type { Game } from "@/features/games/models/game";
import { useInvites } from "@/features/library/hooks/use-invites";
import type { Lobby } from "@/features/lobby/models/lobby";
import { findOwner } from "@/features/lobby/services/lobby";
import InviteCard from "./invite-card";

interface Props {
    game: Game;
}

export default function Invites({ game }: Props) {
    const { invites, loading, error } = useInvites(game.id);

    return (
        <Section title="Invites">
            <State
                loading={loading ?? false}
                error={error ?? false}
                empty={invites.length === 0}
                message="No invites"
            >
                {invites && (
                    <Grid>
                        {invites.map((lobby: Lobby) => {
                            const owner = findOwner(lobby);

                            return (
                                <InviteCard
                                    key={lobby.id}
                                    id={lobby.id}
                                    image={owner.image}
                                    username={owner.username}
                                    players={lobby.players.length}
                                />
                            );
                        })}
                    </Grid>
                )}
            </State>
        </Section>
    );
}
