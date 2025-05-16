import { useState, useEffect, useRef, useCallback } from "react";
import CardList from "../components/CardList.tsx";
import { TagForm } from "../components/TagForm.tsx";
import { PreBoardForm } from "../components/PreBoardForm.tsx";
import { Post } from "../types/Post.ts";
import { Skeleton } from "../components/Skeleton.tsx";
import api from "../api/axiosInstance.ts";
import {Button, Checkbox, Dropdown, DropdownItem } from "flowbite-react";

function Home() {
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [posts, setPosts] = useState<Post[]>([]);
  const [lastPostId, setLastPostId] = useState<number | null>(null);
  const [hasMore, setHasMore] = useState<boolean>(true);
  const [isRecruit, setIsRecruit] = useState<boolean | null>(null);
  const [isFollowing, setIsFollowing] = useState<boolean>(false);
  const [recruitLabel, setRecruitLabel] = useState<string>("모집 여부");
  const observer = useRef<IntersectionObserver | null>(null);

  // 🔵 게시글 API 요청 함수
  const fetchPosts = useCallback(async (reset = false) => {
    if (!hasMore && !reset) {
      console.log("Fetching is halted, no more posts.");
      return;
    }

    try {
      const response = await api.get("/posts", {
        params: {
          lastPostId: reset ? null : lastPostId ?? undefined,
          limit: 10,
          tags: selectedTags.length > 0 ? selectedTags.join(",") : undefined,
          isRecruit: isRecruit ?? undefined, // null이면 모든 게시글, true면 모집중, false면 마감
          isFollowing: isFollowing || undefined, // true일 때만 값을 전송
        },
      });

      if (response.status === 204) {
        console.log("There is no more posts");
        setHasMore(false);
        return;
      }

      const newPosts = response.data.data.posts ?? [];
      console.log("Fetched Posts:", newPosts.length);

      setPosts((prev) => (reset ? newPosts : [...prev, ...newPosts]));

      const newLastPostId = newPosts[newPosts.length - 1]?.post_id ?? null;
      setLastPostId(newLastPostId);

      if (newPosts.length < 10) {
        console.log("Less than 10 posts, no more posts");
        setHasMore(false);
      }
    } catch (error: any) {
      console.error("Error fetching posts:", error);
      setHasMore(false);
    }
  }, [lastPostId, selectedTags, isRecruit, isFollowing]);

  // 🔵 필터 변경 시 초기화
  useEffect(() => {
    setPosts([]);
    setLastPostId(null);
    setHasMore(true);
    fetchPosts(true); // ✅ 초기화 시 리셋 모드로 호출
  }, [selectedTags, isRecruit, isFollowing]);

  // 🔵 무한스크롤 감지 (IntersectionObserver)
  const lastPostRef = useCallback(
      (node: HTMLDivElement) => {
        if (observer.current) observer.current.disconnect();
        console.log("Setting up observer...");

        observer.current = new IntersectionObserver((entries) => {
          if (entries[0].isIntersecting && hasMore) {
            console.log("Observer detected intersection, fetching more posts");
            fetchPosts();
          }
        });

        if (node) {
          if (hasMore) {
            console.log("Observer attached to node");
            observer.current.observe(node);
          } else {
            console.log("Observer not attached because no more posts");
          }
        }
      },
      [hasMore, fetchPosts]
  );

  // 🔵 마지막 페이지에서 무한스크롤 해제
  useEffect(() => {
    if (!hasMore && observer.current) {
      console.log("Disconnecting observer due to no more posts");
      observer.current.disconnect();
    }
  }, [hasMore]);


  return (
      <div className="min-h-screen bg-bright dark:bg-dark">
        <div className="p-4 max-w-3xl mx-auto">
          <PreBoardForm />
          <div className="pt-4">
            <TagForm
                externalTags={selectedTags}
                onTagsChange={(updatedTags) => setSelectedTags(updatedTags)}
            />
          </div>

          {/* 🔵 필터 UI 개선: 우측 정렬, 글자 크기 통일 */}
          <div className="pt-4 flex justify-end items-center gap-4 text-base">
            <div className="flex items-center gap-2">
              <Checkbox
                  id="isFollowing"
                  checked={isFollowing}
                  onChange={(e) => setIsFollowing(e.target.checked)}
              />
              <label htmlFor="isFollowing" >
                팔로잉만 보기
              </label>
            </div>

            <Dropdown label={recruitLabel} inline>
              <DropdownItem onClick={() => {
                setIsRecruit(null);
                setRecruitLabel("모집 여부");
              }}>전체</DropdownItem>
              <DropdownItem onClick={() => {
                setIsRecruit(true);
                setRecruitLabel("모집중");
              }}>모집중</DropdownItem>
              <DropdownItem onClick={() => {
                setIsRecruit(false);
                setRecruitLabel("마감");
              }}>마감</DropdownItem>
            </Dropdown>

            {/* 🔄 필터 초기화 버튼 */}
            <Button size="sm" onClick={() => {
              setIsFollowing(false);
              setIsRecruit(null);
              setRecruitLabel("모집 여부");
              setSelectedTags([]);
            }}>
              필터 초기화
            </Button>
          </div>

          <div className="pt-4">
            <CardList posts={posts} lastPostRef={lastPostRef} />
          </div>
          {hasMore ? <Skeleton /> : <p className="text-center mt-4">No more posts</p>}
        </div>
      </div>
  );
}

export default Home;
