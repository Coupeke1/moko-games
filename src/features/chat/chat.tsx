import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
import BotCard from "@/features/chat/components/channel-card/bot-card";
/* import Message from "@/features/chat/components/message"; */

export default function ChatPage() {
    return (
        <Page>
            <section className={`grid sm:grid-cols-12 ${Gap.Medium}`}>
                <section className="col-span-4 rounded-lg flex flex-col gap-2 h-[calc(100vh-12rem)] overflow-y-scroll">
                    <BotCard selected={true} />
                </section>

                <section className="col-span-8 p-4 rounded-lg bg-bg-3 ">
                    <section className="flex flex-col gap-8 h-[calc(100vh-13rem)] overflow-y-scroll">
                        {/* <Message sender={null} message={null} /> */}
                    </section>
                </section>
            </section>
        </Page>
    );
}
