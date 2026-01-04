import Button from "@/components/buttons/button";
import Card from "@/components/cards/card";
import show from "@/components/global/toast/toast";
import { Type } from "@/components/global/toast/type";
import AcceptIcon from "@/components/icons/accept-icon";
import UsersIcon from "@/components/icons/users-icon";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import { acceptInvite } from "@/features/lobby/services/invites";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router";

interface Props {
    id: string;
    image: string;
    username: string;
    players: number;
}

export default function InviteCard({ id, image, username, players }: Props) {
    const client = useQueryClient();
    const navigate = useNavigate();

    const accept = useMutation({
        mutationFn: async ({ invite }: { invite: string }) =>
            await acceptInvite(invite),
        onSuccess: async () => {
            await client.invalidateQueries({
                queryKey: ["lobby", "invites", id],
            });

            show(Type.Lobby, "Invite accepted");
            navigate(`/lobbies/${id}`);
        },
        onError: (error: Error) => show(Type.Lobby, error.message),
    });

    return (
        <Card
            title={username}
            image={image}
            information={
                <Row gap={Gap.Large} responsive={false}>
                    <Row
                        gap={Gap.Small}
                        items={Items.Center}
                        responsive={false}
                    >
                        <UsersIcon />
                        <p>{players}</p>
                    </Row>
                </Row>
            }
            footer={
                <Button
                    onClick={() => accept.mutate({ invite: id })}
                    fullWidth={true}
                >
                    <AcceptIcon />
                </Button>
            }
        />
    );
}
