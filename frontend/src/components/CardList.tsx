import { CardItem } from "./CardItem";
import {Post} from "../types/Post.ts";

interface CardListProps {
  posts: Post[];
  lastPostRef: (node: HTMLDivElement) => void;
}

const CardList = ({ posts, lastPostRef }: CardListProps) => {
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
