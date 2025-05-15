import { useState, useEffect, useRef, useCallback } from "react";
import CardList from "./components/CardList.tsx";
import { NavBar } from "./components/NavBar.tsx";
import { TagForm } from "./components/TagForm.tsx";
import { PreBoardForm } from "./components/PreBoardForm.tsx";
import {Post} from "./types/Post.ts";
import {Skeleton} from "./components/Skeleton.tsx";
import api from "./api/axiosInstance.ts";

function App() {
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [posts, setPosts] = useState<Post[]>([]);
  const [offset, setOffset] = useState<number>(0);
  const [limit] = useState<number>(10);
  const [hasMore, setHasMore] = useState<boolean>(true);
  const observer = useRef<IntersectionObserver | null>(null);

  const fetchPosts = useCallback(async () => {
    try {
      const response = await api.get("/posts", {
        params: {
          offset,
          limit,
          tags: selectedTags.length > 0 ? selectedTags.join(",") : undefined,
        },
      });

      const newPosts = response.data.data.posts;
      if (offset === 0) setPosts(newPosts);
      else setPosts((prev) => [...prev, ...newPosts]);
      setHasMore(newPosts.length === limit);
    } catch (error) {
      console.error("Error fetching posts:", error);
      setHasMore(false);
    }
  }, [offset, limit, selectedTags]);

  useEffect(() => {
    setOffset(0);
    fetchPosts();
  }, [selectedTags, fetchPosts]);

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
      [hasMore, limit]
  );

  return (
      <div className="min-h-screen bg-bright dark:bg-dark">
        <div className="pt-16">
          <NavBar />
        </div>
        <div className="p-4 max-w-3xl mx-auto">
          <PreBoardForm />
          <div className="pt-4">
            <TagForm onTagsChange={(updatedTags) => setSelectedTags(updatedTags)} />
          </div>
          <div className="pt-4">
            <CardList posts={posts} lastPostRef={lastPostRef} />
          </div>
          {hasMore ? <Skeleton /> : <p className="text-center mt-4">No more posts</p>}
        </div>
      </div>
  );
}

export default App;
