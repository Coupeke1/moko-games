import Card from "@/components/cards/card";
import type { Player } from "@/features/lobby/models/player.ts";
import type { Profile } from "@/features/profiles/models/profile.ts";
import type { ReactNode } from "react";

interface Props {
    user: Profile | Player;
    statistics?: ReactNode;
    footer: ReactNode;
}

export default function UserCard({ user, statistics, footer }: Props) {
    return (
        <Card
            image={user.image}
            title={user.username}
            information={statistics}
            footer={footer}
        />
    );
}
