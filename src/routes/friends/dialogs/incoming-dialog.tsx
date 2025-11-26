import Button from "@/components/buttons/button";
import Dialog from "@/components/dialog/dialog";
import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import Stack from "@/components/layout/stack";
import type { Profile } from "@/models/profile/profile";
import Image from "@/routes/profile/components/image";
import ProfileInformation from "@/routes/profile/components/information";

interface Props {
    request: Profile;
    close: () => void;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function IncomingDialog({ request, close, open, onChange }: Props) {
    return (
        <Dialog title="Incoming Request" open={open} onChange={onChange} footer={
            <Row>
                <Button fullWidth={true}>Accept</Button>
                <Button fullWidth={true}>Reject</Button>
            </Row>
        }>
            <ProfileInformation
                image={request.image}
                username={request.username}
                email={request.email}
                description={request.description}
                level={request.statistics.level}
                playTime={request.statistics.playTime.toString()}
                onEdit={() => {}}
            />
        </Dialog >
    );
}