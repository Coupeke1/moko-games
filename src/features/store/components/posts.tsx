import Card from "@/components/cards/card";
import NewsIcon from "@/components/icons/news-icon";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import Section from "@/components/section";
import ErrorState from "@/components/state/error";
import State from "@/components/state/state";
import PostDialog from "@/features/store/dialogs/post-dialog";
import { usePosts } from "@/features/store/hooks/use-posts.ts";
import type { Entry } from "@/features/store/models/entry/entry.ts";
import type { Post } from "@/features/store/models/post/post";
import { format } from "@/features/store/models/post/type";
import { useState } from "react";

interface Props {
    entry: Entry;
}

export default function Posts({ entry }: Props) {
    const { posts, loading, error } = usePosts(entry.id);
    const [post, setPost] = useState(false);
    const [selected, setSelected] = useState<Post | null>(null);

    const select = (post: Post) => {
        setSelected(post);
        setPost(true);
    };

    return (
        <>
            <State data={posts} loading={loading} error={error} />

            {posts && selected && (
                <PostDialog post={selected} open={post} onChange={setPost} />
            )}

            {posts && posts.length > 0 && (
                <Section title="Posts">
                    <Grid>
                        {posts.map((post: Post) => (
                            <Card
                                key={post.id}
                                title={post.title}
                                image={post.image}
                                onClick={() => select(post)}
                                information={
                                    <Row
                                        gap={Gap.Small}
                                        items={Items.Center}
                                        responsive={false}
                                    >
                                        <NewsIcon />
                                        <p>{format(post.type)}</p>
                                    </Row>
                                }
                            />
                        ))}
                    </Grid>
                </Section>
            )}
        </>
    );
}
