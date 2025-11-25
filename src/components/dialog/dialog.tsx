import SmallButton from "@/components/dialog/small-button";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import Row from "@/components/layout/row";
import * as RadixDialog from "@radix-ui/react-dialog";
import type { ReactNode } from "react";
import CloseIcon from "../icons/bar/close-icon";

interface Props {
    title: string;
    open: boolean;
    onChange: (open: boolean) => void;
    onClose?: () => void;
    children: ReactNode;
}

export default function Dialog({ title, open, onChange, onClose, children }: Props) {
    const handleOpenChange = (open: boolean) => {
        onChange(open);
        if (!open && onClose) onClose();
    };

    const handleCloseClick = () => {
        onChange(false);
        if (onClose) onClose();
    };

    return (
        <RadixDialog.Root open={open} onOpenChange={handleOpenChange}>
            <RadixDialog.Portal>
                <RadixDialog.Overlay className="fixed inset-0 bg-black/50 z-50" />
                <RadixDialog.Content className="flex flex-col gap-2 overflow-y-scroll fixed left-1/2 top-1/2 xs:w-[75%] w-[90%] max-w-2xl h-[90%] -translate-x-1/2 -translate-y-1/2 rounded-lg bg-bg text-fg border-3 border-fg-2 py-4 px-6 z-50">
                    <Row justify={Justify.Between} items={Items.Center} responsive={false}>
                        <RadixDialog.Title className="text-xl font-semibold">{title}</RadixDialog.Title>
                        <RadixDialog.Close asChild onClick={handleCloseClick}>
                            <SmallButton>
                                <CloseIcon />
                            </SmallButton>
                        </RadixDialog.Close>
                    </Row>

                    {children}
                </RadixDialog.Content>
            </RadixDialog.Portal>
        </RadixDialog.Root>
    );
}