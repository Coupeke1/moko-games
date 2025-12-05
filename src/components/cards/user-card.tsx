import Card from "@/components/cards/card";
import type { Player } from "@/models/lobby/player";
import type { Profile } from "@/models/profile/profile";
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
