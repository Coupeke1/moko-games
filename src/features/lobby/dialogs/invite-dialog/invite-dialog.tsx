import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import type { Lobby } from "@/features/lobby/models/lobby.ts";
import BotsSection from "@/features/lobby/dialogs/invite-dialog/bots-section";
import FriendsSection from "@/features/lobby/dialogs/invite-dialog/friends-section";

interface Props {
    lobby: Lobby;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function InviteDialog({ lobby, close, open, onChange }: Props) {
    return (
        <Dialog title="Invite" open={open} onChange={onChange}>
            <Column gap={Gap.Large}>
                <Grid>
                    <BotsSection lobby={lobby} onInvite={close} />
                    <FriendsSection lobby={lobby} onInvite={close} />
                </Grid>
            </Column>
        </Dialog>
    );
}
