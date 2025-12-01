import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import TabContent from "@/components/tabs/buttons/content";
import TabRow from "@/components/tabs/buttons/row";
import type { Lobby } from "@/models/lobby/lobby";
import FriendsTab from "@/routes/lobby/dialogs/invite-dialog/friends-tab";
import BotsTab from "@/routes/lobby/dialogs/invite-dialog/bots-tab";
import { useState } from "react";

interface Props {
    lobby: Lobby;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function InviteDialog({ lobby, open, onChange }: Props) {
    const [current, setCurrent] = useState<string>("Friends");

    return (
        <Dialog
            title="Invite"
            onClose={() => setCurrent("Friends")}
            open={open}
            onChange={onChange}
        >
            <Column gap={Gap.Large}>
                <TabRow
                    tabs={["Friends", "Bots"]}
                    current={current}
                    setCurrent={setCurrent}
                />

                <TabContent
                    current={current}
                    tabs={[
                        {
                            title: "Friends",
                            element: <FriendsTab lobby={lobby} />,
                        },
                        {
                            title: "Bots",
                            element: <BotsTab lobby={lobby} />,
                        },
                    ]}
                />
            </Column>
        </Dialog>
    );
}
