import { usePlayerProfile } from '@/hooks/use-player-profile';
import type { Player } from '@/models/player';
import { clsx } from 'clsx';
import PlayerImage from './player-image';

interface Props {
    player: Player;
    currentTurn?: boolean;
}

export default function PlayerCard({ player, currentTurn = false }: Props) {
    const { data: profile, isLoading: playerLoading } = usePlayerProfile(player.id);


    return (
        <div className={clsx("flex items-center justify-between px-6 py-4 rounded-lg bg-bg-2", currentTurn && "bg-bg-3 ring-2 ring-fg-2")}>
            <div className='flex items-center gap-4'>
                {playerLoading
                    ? <>
                        <PlayerImage loading />
                    </>
                    : <>
                        <PlayerImage src={profile?.image} />
                        <p className='text-fg text-lg font-semibold'>{profile?.username}</p>
                    </>
                }
            </div>
            <p className='text-5xl'>
                {player.role}
            </p>
        </div>
    )
}