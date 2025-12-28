import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
import BotCard from "@/features/chat/components/channel-card/bot-card";
import Friends from "@/features/chat/components/friends";
import SendBar from "@/features/chat/components/send-bar";
/* import Message from "@/features/chat/components/message"; */

export default function ChatPage() {
    return (
        <Page>
            <section className={`grid md:grid-cols-12 ${Gap.Medium}`}>
                <section className="col-span-12 md:col-span-4 h-full rounded-lg flex flex-col gap-2 md:h-[calc(100vh-12rem)] overflow-y-scroll">
                    <BotCard selected={true} />
                    <Friends />
                </section>

                <section
                    className={`flex flex-col ${Gap.Medium} col-span-12 md:col-span-8`}
                >
                    <section className="p-4 rounded-lg bg-bg-3 flex flex-col gap-8 h-32 md:h-[calc(100vh-13rem)] overflow-y-scroll">
                        {/* <Message sender={null} message={null} /> */}
                    </section>

                    <SendBar
                        onSend={(message: string) => {
                            console.log(message);
                        }}
                    />
                </section>
            </section>
        </Page>
    );
}
