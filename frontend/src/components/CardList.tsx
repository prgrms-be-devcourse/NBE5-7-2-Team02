import { CardItem } from "./CardItem";
import { Post } from "../types/Post";

interface CardListProps {
  posts: Post[];
  lastPostRef: (node: HTMLDivElement) => void;
}

const CardList = ({ posts, lastPostRef }: CardListProps) => {
  if (posts.length === 0) {
    return;
  }

  return (
      <div className="grid gap-4">
        {posts.map((post, index) => (
            <div
                key={post.post_id}
                ref={index === posts.length - 1 ? lastPostRef : null}
            >
              <CardItem post={post} />
            </div>
        ))}
      </div>
  );
};

export default CardList;