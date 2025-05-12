// src/components/CardList.tsx
import React, { useState, useEffect, useRef } from "react";
import CardItem from "./CardItem";

interface Post {
  id: number;
  title: string;
  description: string;
}

const CardList: React.FC = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const loader = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    loadMorePosts();
  }, []);

  useEffect(() => {
    const observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            loadMorePosts();
          }
        },
        {
          rootMargin: "100px",
          threshold: 1.0,
        }
    );

    if (loader.current) {
      observer.observe(loader.current);
    }

    return () => {
      if (loader.current) {
        observer.unobserve(loader.current);
      }
    };
  }, []);

  const loadMorePosts = () => {
    const newPosts = Array.from({ length: 5 }, (_, index) => ({
      id: posts.length + index + 1,
      title: `Post Title ${posts.length + index + 1}`,
      description: "This is a dynamically loaded post description.",
    }));

    setPosts((prev) => [...prev, ...newPosts]);
  };

  return (
      <div className="flex flex-col gap-4 w-full">
        {posts.map((post) => (
            <CardItem
                key={post.id}
                title={post.title}
                description={post.description}
            />
        ))}
        <div ref={loader} className="h-8" />
      </div>
  );
};

export default CardList;
