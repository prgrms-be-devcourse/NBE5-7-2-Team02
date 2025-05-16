import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Member } from "../types/Member";
import { Follow } from "../types/Follow";
import FollowSummary from "../components/mypage/FollowSummary";
import FollowerModal from "../components/mypage/FollowerModal";
import FollowingModal from "../components/mypage/FollowingModal";
import ProfileEditorModal from "../components/mypage/ProfileEditorModal";
import MyPostList from "../components/mypage/MyPostList";
import api from "../api/axiosInstance";


export default function MyPage() {
  const { userId } = useParams();
  const [user, setUser] = useState<Member | null>(null);
  const [showFollowers, setShowFollowers] = useState(false);
  const [showFollowings, setShowFollowings] = useState(false);
  const [showProfileEdit, setShowProfileEdit] = useState(false);
  const [followers, setFollowers] = useState<Follow[]>([]);
  const [followings, setFollowings] = useState<Follow[]>([]);

// ✅ 사용자 정보 조회
  const fetchUser = async () => {
    try {
      const res = await api.get(`/member/${userId || "me"}`);
      const raw = res.data.data;

      const user: Member = {
        id: raw.memberId,
        username: raw.name,
        profileImage: raw.profileImage,
        followerCount: raw.followerCount,
        followingCount: raw.followingCount,
        isFollowing: raw.isFollowing,
        isOwner: raw.isOwner,
      };

      setUser(user);
    } catch (e) {
      console.error("사용자 정보 조회 실패", e);
    }
  };

  useEffect(() => {
    fetchUser();
  }, [userId]);

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

      <MyPostList userId={user.id} />

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
  );
}
