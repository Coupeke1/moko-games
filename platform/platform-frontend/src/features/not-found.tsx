import Page from "@/components/layout/page";
import Paragraph from "@/components/text/paragraph";
import Title from "@/components/text/title";

export default function NotFoundPage() {
    return (
        <Page>
            <Title>Oops, that page was not found!</Title>
            <Paragraph>Try going back to the previous page...</Paragraph>
        </Page>
    );
}