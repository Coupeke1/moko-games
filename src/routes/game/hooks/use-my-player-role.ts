import type {Player} from "@/routes/game/model/Player.ts";

export function useMyPlayerRole(players: Player[] | undefined, playerId: string | undefined) {
    if (!players || players.length <= 0 || !playerId) {
        return null;
    }

    const myPlayer = players.find(player => player.id === playerId);
    return myPlayer?.role || null;
}