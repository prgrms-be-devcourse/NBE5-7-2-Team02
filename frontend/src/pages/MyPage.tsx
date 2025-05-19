import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { InfiniteScroll } from "../components/InfiniteScroll";
import CardList from "../components/CardList";
import { Member } from "../types/Member";
import { Follow } from "../types/Follow";
import FollowSummary from "../components/mypage/FollowSummary";
import FollowListModal from "../components/mypage/FollowListModal";
import ProfileEditorModal from "../components/mypage/ProfileEditorModal";
import api from "../api/axiosInstance";


export default function MyPage() {
  const navigate = useNavigate();
  const [ searchParams ] = useSearchParams();
  const memberId = searchParams.get("memberId");
  const [limit] = useState<number>(10);
  const [user, setUser] = useState<Member | null>(null);
  const [showFollowers, setShowFollowers] = useState(false);
  const [showFollowings, setShowFollowings] = useState(false);
  const [showProfileEdit, setShowProfileEdit] = useState(false);
  const [followers, setFollowers] = useState<Follow[]>([]);
  const [followings, setFollowings] = useState<Follow[]>([]);

// ✅ 사용자 정보 조회
  const fetchUser = async () => {
    try {
      const res = await api.get(`/member/${memberId || "me"}`);
      const raw = res.data.data;

      const user: Member = {
        id: raw.member_id,
        username: raw.name,
        profileImage: raw.profile_image,
        followerCount: raw.follower_count,
        followingCount: raw.following_count,
        isFollowing: raw.is_following,
        isOwner: raw.is_owner,
      };

      setUser(user);
    } catch (e) {
      console.error("사용자 정보 조회 실패", e);
    }
  };

  useEffect(() => {
    fetchUser();
  }, [memberId]);

  // ✅ 팔로우 / 언팔로우 요청
  const handleFollowToggle = async () => {
    if (!user) return;

    try {
      if (user.isFollowing) {
        await api.delete(`/follow/${user.id}`);
      } else {
        await api.post(`/follow/${user.id}`);
      }
      setUser((prev) => prev && { ...prev, isFollowing: !prev.isFollowing });
    } catch (e) {
      console.error("팔로우 상태 변경 실패", e);
    }
  };

  // ✅ 프로필 저장
  const handleSaveProfile = async (nickname: string, image: string) => {
    try {
      await api.put(`/member/me`, { nickname, image });
      setUser((prev) =>
        prev ? { ...prev, username: nickname, profileImage: image } : prev
      );
      setShowProfileEdit(false);
    } catch (e) {
      console.error("프로필 저장 실패", e);
    }
  };

  // ✅ 팔로워 목록 조회
  const fetchFollowers = async () => {
    if (!user) return;
    const res = await api.get(
      user.isOwner
        ? `/follow/me/followers`
        : `/follow/public/${user.id}/followers`
    );
    const list: Follow[] = res.data.data.content.map((f: any) => ({
      id: f.id,
      username: f.name,
      profileImage: f.profileImage,
    }));
    setFollowers(list);
    setShowFollowers(true);
  };

  // ✅ 팔로잉 목록 조회
  const fetchFollowings = async () => {
    if (!user) return;
    const res = await api.get(
      user.isOwner
        ? `/follow/me/followings`
        : `/follow/public/${user.id}/followings`
    );
    const list: Follow[] = res.data.data.content.map((f: any) => ({
      id: f.id,
      username: f.name,
      profileImage: f.profileImage,
    }));
    setFollowings(list);
    setShowFollowings(true);
  };

  if (!user) {
    return <p className="text-center mt-10 text-gray-500">로딩 중...</p>;
  }

  return (
      <div className="min-h-screen bg-bright dark:bg-dark">
        <div className="p-4 max-w-2xl mx-auto">
          <FollowSummary
              username={user.username}
              profileImage={user.profileImage}
              followerCount={user.followerCount}
              followingCount={user.followingCount}
              isOwner={user.isOwner}
              isFollowing={user.isFollowing}
              onEditProfile={() => setShowProfileEdit(true)}
              onFollowToggle={handleFollowToggle}
              onShowFollowers={fetchFollowers}
              onShowFollowings={fetchFollowings}
          />

          <FollowListModal
              show={showFollowers}
              onClose={() => setShowFollowers(false)}
              title="팔로워"
              users={followers}
              onProfileClick={(id) => navigate(`/mypage/${id}`)}
          />

          <FollowListModal
              show={showFollowings}
              onClose={() => setShowFollowings(false)}
              title="팔로잉"
              users={followings}
              onProfileClick={(id) => navigate(`/mypage/${id}`)}
          />

          <ProfileEditorModal
              show={showProfileEdit}
              onClose={() => setShowProfileEdit(false)}
              username={user.username}
              profileImage={user.profileImage}
              onSave={handleSaveProfile}
          />
        </div>
        <div className="p-4 max-w-3xl mx-auto">
          <h2 className="text-2xl font-bold mb-4">My Posts</h2>
          <InfiniteScroll
              apiEndpoint={`/posts/${memberId}`}
              limit={limit}
              fetchKey={`member-${memberId}`} // Add a unique key based on memberId
              renderPosts={(posts, lastPostRef) => (
                  <CardList posts={posts} lastPostRef={lastPostRef} />
              )}
          />
        </div>
      </div>
  );
}

