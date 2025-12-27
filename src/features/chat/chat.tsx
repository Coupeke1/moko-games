import { Gap } from "@/components/layout/gap";
import Page from "@/components/layout/page";
/* import FriendCard from "@/features/chat/components/friend-card"; */
/* import Message from "@/features/chat/components/message"; */

export default function ChatPage() {
    return (
        <Page>
            <section className={`grid sm:grid-cols-12 ${Gap.Medium}`}>
                <section className="col-span-4 rounded-lg flex flex-col gap-2 h-[calc(100vh-12rem)] overflow-y-scroll">
                    {/* <FriendCard friend={null} selected={true} /> */}
                </section>

                <section className="col-span-8 p-4 rounded-lg bg-bg-3 flex flex-col gap-8 h-[calc(100vh-12rem)] overflow-y-scroll">
                    {/* <Message sender={null} message={null} /> */}
                </section>
            </section>
        </Page>
    );
}
