import { useState, useEffect, useRef, useCallback, JSX } from "react";
import { Skeleton } from "@/components/ui/skeleton";
import api from "../api/axiosInstance";

interface InfiniteScrollProps {
  apiEndpoint: string;
  queryParams?: Record<string, any>;
  fetchKey?: string;
  limit?: number;
  lastPostId?: number | null;
  renderPosts: (posts: any[], lastPostRef: (node: HTMLDivElement) => void) => JSX.Element;
}

export const InfiniteScroll = ({
                                 apiEndpoint,
                                 queryParams = {},
                                 fetchKey = "",
                                 limit = 10,
                                 lastPostId = null,
                                 renderPosts
                               }: InfiniteScrollProps) => {
  const [posts, setPosts] = useState<any[]>([]);
  const [currentLastPostId, setCurrentLastPostId] = useState<number | null>(lastPostId);
  const [hasMore, setHasMore] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const observer = useRef<IntersectionObserver | null>(null);

  // Use a single fetch function with proper loading state management
  const fetchPosts = useCallback(async (reset = false) => {
    if (isLoading || (!hasMore && !reset)) return;

    setIsLoading(true);

    try {
      const response = await api.get(apiEndpoint, {
        params: {
          lastPostId: reset ? null : currentLastPostId ?? undefined,
          limit,
          ...queryParams,
        },
      });

      // Handle both success and empty responses
      if (response.status === 204) {
        setHasMore(false);
        setIsLoading(false);
        return;
      }

      const newPosts = response.data.posts ?? [];

      // Handle the case where there are no posts
      if (newPosts.length === 0) {
        setHasMore(false);
        setIsLoading(false);
        return;
      }

      // Update posts state
      setPosts((prev) => (reset ? newPosts : [...prev, ...newPosts]));

      // Check for post_id or id field in the response
      const lastPost = newPosts[newPosts.length - 1];
      const nextLastPostId = lastPost?.post_id ?? lastPost?.id ?? null;
      setCurrentLastPostId(nextLastPostId);

      // Check if we've reached the end
      setHasMore(newPosts.length >= limit);
    } catch (error) {
      console.error("Error fetching posts:", error);
      setHasMore(false);
    } finally {
      setIsLoading(false);
    }
  }, [apiEndpoint, queryParams, limit, currentLastPostId, hasMore, isLoading]);

  // Reset everything when key parameters change
  useEffect(() => {
    setHasMore(true);
    setPosts([]);
    setCurrentLastPostId(null);
    fetchPosts(true);
  }, [fetchKey, apiEndpoint]); // Only depend on fetchKey and apiEndpoint for resets

  // Reset when queryParams change
  useEffect(() => {
    if (JSON.stringify(queryParams) !== '{}') {
      setHasMore(true);
      setPosts([]);
      setCurrentLastPostId(null);
      fetchPosts(true);
    }
  }, [JSON.stringify(queryParams)]);

  // Set up the intersection observer for infinite scrolling
  const lastPostRef = useCallback(
      (node: HTMLDivElement) => {
        if (observer.current) observer.current.disconnect();

        observer.current = new IntersectionObserver((entries) => {
          if (entries[0].isIntersecting && hasMore && !isLoading) {
            fetchPosts();
          }
        });

        if (node) observer.current.observe(node);
      },
      [fetchPosts, hasMore, isLoading]
  );

  return (
      <div className="w-full">
        {renderPosts(posts, lastPostRef)}
        {isLoading && (
            <div className="space-y-3 mt-4">
              <Skeleton className="h-[125px] w-full rounded-lg bg-muted/40" />
              <Skeleton className="h-4 w-1/2 bg-muted/40" />
              <Skeleton className="h-4 w-3/4 bg-muted/40" />
            </div>
        )}
        {!hasMore && posts.length > 0 && <p className="text-center mt-4 text-muted-foreground">No more posts</p>}
      </div>
  );
};