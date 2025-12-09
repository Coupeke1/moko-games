import Dialog from "@/components/dialog/dialog";
import type { Post } from "@/features/store/models/post/post";
import DOMPurify from "dompurify";

interface Props {
    post: Post;
    open: boolean;
    onChange: (open: boolean) => void;
}

export default function PostDialog({ post, open, onChange }: Props) {
    return (
        <Dialog title={post.title} open={open} onChange={onChange}>
            <section
                className="flex flex-col gap-2"
                dangerouslySetInnerHTML={{
                    __html: DOMPurify.sanitize(post.content),
                }}
            />
        </Dialog>
    );
}
