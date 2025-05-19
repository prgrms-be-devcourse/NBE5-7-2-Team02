// components/mypage/MyPostList.tsx
import { useEffect, useRef, useCallback, useState } from "react";
import { Card } from "flowbite-react";
import api from "../../api/axiosInstance";

interface Post {
  id: number;
  title: string;
  content: string;
}

interface Props {
  userId: number;
}

export default function MyPostList({ userId }: Props) {
  const [posts, setPosts] = useState<Post[]>([]);
  const [offset, setOffset] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const limit = 10;
  const observer = useRef<IntersectionObserver | null>(null);

  const lastPostRef = useCallback(
    (node: HTMLDivElement) => {
      if (observer.current) observer.current.disconnect();
      observer.current = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && hasMore) {
          setOffset((prev) => prev + limit);
        }
      });
      if (node) observer.current.observe(node);
    },
    [hasMore]
  );

  const fetchPosts = useCallback(async () => {
    try {
      const res = await api.get(`/posts/user/${userId}`, {
        params: { offset, limit },
      });
      const newPosts = res.data?.data || [];
      setPosts((prev) => [...prev, ...newPosts]);
      setHasMore(newPosts.length === limit);
    } catch (err) {
      console.error("내 게시글 불러오기 실패", err);
    }
  }, [userId, offset]);

  useEffect(() => {
    fetchPosts();
  }, [fetchPosts]);

  if (posts.length === 0) return <p className="text-center text-gray-500">작성한 글이 없습니다.</p>;

  return (
    <div className="space-y-4 mt-6">
      {posts.map((post, index) => (
        <div
          key={post.id}
          ref={index === posts.length - 1 ? lastPostRef : null}
        >
          <Card>
            <h3 className="text-xl font-bold">{post.title}</h3>
            <p>{post.content}</p>
          </Card>
        </div>
      ))}
    </div>
  );
}
