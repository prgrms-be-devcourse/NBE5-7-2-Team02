import { Post } from "@/types/Post.ts";
import { MyCardItem } from "./MyCardItem";

interface Props {
  posts: Post[];
  setPosts: React.Dispatch<React.SetStateAction<Post[]>>;
  lastPostRef: (node: HTMLDivElement) => void;
}

export const MyCardList = ({ posts, setPosts, lastPostRef }: Props) => {
  const handleDelete = (postId: number) => {
    setPosts((prev) => prev.filter((post) => post.post_id !== postId));
  };

  return (
    <div className="grid gap-4">
      {posts.map((post, index) => (
        <div
          key={post.post_id}
          ref={index === posts.length - 1 ? lastPostRef : null}
        >
          <MyCardItem post={post} onDelete={handleDelete} />
        </div>
      ))}
    </div>
  );
};