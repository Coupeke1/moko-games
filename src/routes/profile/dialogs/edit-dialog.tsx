import Dialog from "@/components/dialog/dialog";
import Input from "@/components/inputs/input";
import Column from "@/components/layout/column";
import Grid from "@/components/layout/grid/grid";
import { GridSize } from "@/components/layout/grid/size";
import type { Profile } from "@/models/profile";

interface Props {
    profile: Profile;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function EditDialog({ profile, open, onChange }: Props) {
    return (
        <Dialog title="Profile Settings" open={open} onChange={onChange}>
            <Grid size={GridSize.Small}>
                <Input label="Username" disabled={true} value={profile.username} onChange={() => {}} />
                <Input label="Email" disabled={true} value={profile.email} onChange={() => {}} />
            </Grid>

            
        </Dialog>
    );

}