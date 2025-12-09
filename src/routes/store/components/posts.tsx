import Card from "@/components/cards/card";
import NewsIcon from "@/components/icons/news-icon";
import { Gap } from "@/components/layout/gap";
import Grid from "@/components/layout/grid/grid";
import { GridSize } from "@/components/layout/grid/size";
import { Items } from "@/components/layout/items";
import Row from "@/components/layout/row";
import State from "@/components/state/state";
import { usePosts } from "@/hooks/use-posts";
import type { Entry } from "@/models/store/entry";
import type { Post } from "@/models/store/post/post";
import { format } from "@/models/store/post/type";
import PostDialog from "@/routes/store/dialogs/post-dialog";
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

            {posts && (
                <Grid size={GridSize.Small}>
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
            )}
        </>
    );
}
