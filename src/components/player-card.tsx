import { usePlayerProfile } from '@/hooks/use-player-profile';
import type { Player } from '@/models/player';
import { clsx } from 'clsx';
import PlayerImage from './player-image';
import Cross from './icons/cross';
import Circle from './icons/circle';

interface Props {
    player: Player;
    currentTurn?: boolean;
}

export default function PlayerCard({ player, currentTurn = false }: Props) {
    const { data: profile, isLoading: playerLoading } = usePlayerProfile(player.id);


    return (
        <div className={clsx("flex items-center justify-between px-6 py-4 rounded-lg bg-bg-2", currentTurn && "bg-bg-3 ring-2 ring-fg-2")}>
            <div className='flex items-center gap-4'>
                {playerLoading || !profile
                    ? <>
                        <PlayerImage loading />
                        <span className='w-48 h-6 rounded-md bg-fg-2 animate-pulse'></span>
                    </>
                    : <>
                        <PlayerImage src={profile?.image} />
                        <p className='text-fg text-lg font-semibold'>{profile?.username}</p>
                    </>
                }
            </div>
            {player.role === 'X' ? <Cross /> : <Circle />}
        </div>
    )
}