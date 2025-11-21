import Column from "@/components/layout/column";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import Page from "@/components/layout/page";
import Section from "@/components/section";
import AchievementCard from "@/routes/profile/components/achievement-card";
import FriendCard from "@/routes/profile/components/friend-card";
import ProfileInformation from "@/routes/profile/components/information";

const profileImage = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fhips.hearstapps.com%2Fhmg-prod%2Fimages%2Fhow-to-keep-ducks-call-ducks-1615457181.jpg%3Fresize%3D2048%3A*&f=1&nofb=1&ipt=05b3fac00194ed3070012ebda1a671c31e59553b92b5373e1e361522b4f74491";

export default function ProfilePage() {
    return (
        <Page>
            <Column gap={Gap.ExtraLarge}>
                <ProfileInformation
                    image={profileImage}
                    username="niceduckbro"
                    email="kaj.botty@student.kdg.be"
                    description="Lorem ipsum dolor sit amet consectetur adipisicing elit. Facere odit culpa cumque illum commodia exercitationem mollitia eligendi. Unde sequi facere nostrum rerum. Voluptatibus amet praesentium esse quos veniam maiores quaerat aliquid voluptates ad, at consectetur similique sint reprehenderit soluta libero voluptatum aperiam eaque accusamus dolore vitae aut! Ea, autem."
                    level={124}
                    playtime="365h 23m"
                />

                <Section title="Friends">
                    <Grid>
                        <FriendCard
                            image={profileImage}
                            username="niceduckbro"
                            level={33}
                            playtime="322h 44m"
                        />

                        <button className="bg-bg-2 hover:bg-bg-3 transition-colors duration-75 flex flex-col justify-center items-center cursor-pointer select-none x-4 py-2 rounded-lg h-48">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="size-8 text-fg-2">
                                <path fillRule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25ZM12.75 9a.75.75 0 0 0-1.5 0v2.25H9a.75.75 0 0 0 0 1.5h2.25V15a.75.75 0 0 0 1.5 0v-2.25H15a.75.75 0 0 0 0-1.5h-2.25V9Z" clipRule="evenodd" />
                            </svg>
                        </button>
                    </Grid>
                </Section>

                <Section title="Achievements">
                    <Column>
                        <AchievementCard
                            image={"https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fimg.opencritic.com%2Fgame%2F6846%2Fo%2FOynP2Fqx.jpg&f=1&nofb=1&ipt=371f8470bf154c2bf29073bc3c6ee6b8d0a1fbbfb728b1e7b20538e5266af57a"}
                            title="The Ends and the Means"
                            description="Discover what happened to NERO"
                            date="7/10/23"
                            game="Days Gone"
                        />
                    </Column>
                </Section>
            </Column>
        </Page>
    )
}