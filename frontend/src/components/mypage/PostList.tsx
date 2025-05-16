// components/mypage/PostList.tsx
import { useNavigate } from "react-router-dom";
import { Card, Button } from "flowbite-react";

interface Post {
  id: number;
  title: string;
  content: string;
}

interface Props {
  posts: Post[];
}

export default function PostList({ posts }: Props) {
  const navigate = useNavigate();

  if (!posts || posts.length === 0) {
    return <p className="text-center text-gray-500">작성한 글이 없습니다.</p>;
  }

  return (
    <div className="mt-8 space-y-4">
      <h2 className="text-2xl font-semibold">내가 작성한 글</h2>
      {posts.map((post) => (
        <Card key={post.id} className="mb-4">
          <h3 className="text-xl font-bold">{post.title}</h3>
          <p>{post.content}</p>
          <Button onClick={() => navigate(`/post/${post.id}`)} className="mt-2">
            자세히 보기
          </Button>
        </Card>
      ))}
    </div>
  );
}
