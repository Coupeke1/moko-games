import SmallButton from "@/components/dialog/small-button";
import CloseIcon from "@/components/icons/bar/close-icon";
import Column from "@/components/layout/column";
import { Items } from "@/components/layout/items";
import { Justify } from "@/components/layout/justify";
import Row from "@/components/layout/row";
import Stack from "@/components/layout/stack";
import * as RadixDialog from "@radix-ui/react-dialog";
import type { ReactNode } from "react";

interface Props {
    title: string;
    open: boolean;
    onChange: (open: boolean) => void;
    onClose?: () => void;
    header?: ReactNode;
    children: ReactNode;
    footer?: ReactNode;
}

export default function Dialog({
    title,
    open,
    onChange,
    onClose,
    header,
    children,
    footer,
}: Props) {
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
                <RadixDialog.Overlay className="fixed inset-0 bg-black/50 z-100" />
                <RadixDialog.Content className="flex flex-col fixed z-100 left-1/2 top-1/2 xs:w-[75%] w-[90%] max-w-2xl h-[90%] -translate-x-1/2 -translate-y-1/2 rounded-lg bg-bg text-fg overflow-hidden">
                    <section className="sticky top-0 bg-bg py-3 px-4 border-b-2 border-bg-2 z-10">
                        <Column>
                            <Row
                                justify={Justify.Between}
                                items={Items.Center}
                                responsive={false}
                            >
                                <RadixDialog.Title className="text-xl font-semibold truncate">
                                    {title}
                                </RadixDialog.Title>
                                <RadixDialog.Close
                                    asChild
                                    onClick={handleCloseClick}
                                >
                                    <SmallButton>
                                        <CloseIcon />
                                    </SmallButton>
                                </RadixDialog.Close>
                            </Row>

                            {header && header}
                        </Column>
                    </section>

                    <section className="flex-1 overflow-y-auto px-6 py-4">
                        {children}
                    </section>

                    {footer && (
                        <section className="sticky bottom-0 bg-bg py-4 px-6 border-t-2 border-bg-2 z-10">
                            <Stack>{footer}</Stack>
                        </section>
                    )}
                </RadixDialog.Content>
            </RadixDialog.Portal>
        </RadixDialog.Root>
    );
}
