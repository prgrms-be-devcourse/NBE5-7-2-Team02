import { useState } from "react";
import { InfiniteScroll } from "../components/InfiniteScroll";
import CardList from "../components/CardList";
import { TagForm } from "../components/TagForm";
import { PreBoardForm } from "../components/PreBoardForm";
import { FilterOptions } from "../components/FilterOptions";
import { useAuth } from "../context/AuthContext";

function Home() {
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [isRecruit, setIsRecruit] = useState<boolean | null>(null);
  const [isFollowing, setIsFollowing] = useState<boolean>(false);
  const [limit] = useState<number>(10);
  const [lastPostId] = useState<number | null>(null);
  const { isAuthenticated } = useAuth();

  const handleResetFilters = () => {
    setSelectedTags([]);
    setIsRecruit(null);
    setIsFollowing(false);
  };

  const queryParams = {
    tags: selectedTags.length > 0 ? selectedTags.join(",") : undefined,
    isRecruit: isRecruit ?? undefined,
    isFollowing: isFollowing || undefined,
  };

  return (
      <div className="min-h-screen bg-bright dark:bg-dark">
        <div className="p-4 max-w-3xl mx-auto">
          {isAuthenticated ? <PreBoardForm /> : null}

          <div className="mt-4">
            <TagForm
                externalTags={selectedTags}
                onTagsChange={(updatedTags) => setSelectedTags(updatedTags)}
            />
          </div>

          <div className="mt-4">
            <FilterOptions
                isRecruit={isRecruit}
                isFollowing={isFollowing}
                onRecruitChange={setIsRecruit}
                onFollowingChange={setIsFollowing}
                onReset={handleResetFilters}
            />
          </div>

          <InfiniteScroll
              apiEndpoint="/posts"
              queryParams={queryParams}
              fetchKey={JSON.stringify(queryParams)}
              limit={limit}
              lastPostId={lastPostId}
              renderPosts={(posts, lastPostRef) => (
                  <CardList posts={posts} lastPostRef={lastPostRef} />
              )}
          />
        </div>
      </div>
  );
}

export default Home;
