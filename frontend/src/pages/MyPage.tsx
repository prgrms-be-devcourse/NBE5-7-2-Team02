import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { InfiniteScroll } from "../components/InfiniteScroll";
import CardList from "../components/CardList";
import { Member } from "../types/Member";
import { Follow } from "../types/Follow";
import FollowSummary from "../components/mypage/FollowSummary";
import FollowListModal from "../components/mypage/FollowListModal";
import ProfileEditorModal from "../components/mypage/ProfileEditorModal";
import api from "../api/axiosInstance";
import { useAuth } from "../context/AuthContext";


export default function MyPage() {
  const navigate = useNavigate();
  const { setUser: setAuthUser } = useAuth();
  const [memberId, setMemberId] = useState<string | null>(null);
  const { postMemberId } = useParams();
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
      const res = await api.get(`/member/${postMemberId || "me"}`);
      const raw = res.data;

      const fetchedUser: Member = {
        id: raw.id,
        username: raw.name,
        profileImage: raw.profile_image,
        followerCount: raw.follower_count,
        followingCount: raw.following_count,
        following: raw.following,
        owner: raw.owner
      };
      
      setUser(fetchedUser);
      setMemberId(raw.id?.toString()); // 여기가 InfiniteScroll에 들어감

    } catch (e) {
      console.error("사용자 정보 조회 실패", e);
    }
  };

  useEffect(() => {
    fetchUser();
  }, [postMemberId]);

  // ✅ 팔로우 / 언팔로우 요청 및 수치 반영
  const handleFollowToggle = async () => {
    if (!user) return;
    try {
      let res;
      if (user.following) {
        await api.delete(`/follow/${user.id}`);
        // 팔로우 취소 시 count는 줄여줌
        setUser((prev) => prev && {
          ...prev,
          following: false,
          followerCount: prev.followerCount - 1
        });
      } else {
        res = await api.post(`/follow/${user.id}`);
        const data = res.data;
        setUser((prev) => prev && {
          ...prev,
          following: true,
          followerCount: data.updated_follower_count,
        });
      }
    } catch (e) {
      console.error("팔로우 상태 변경 실패", e);
    }
  };

const handleSaveProfile = async (nickname: string, image: File | null) => {
  try {
    const formData = new FormData();
    formData.append("nickname", nickname);
    if (image) formData.append("image", image);

    const res = await api.patch(`/member/me`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    const raw = res.data;
    const updatedUser: Member = {
      id: raw.id,
      username: raw.name,
      profileImage: raw.profile_image,
      followerCount: raw.follower_count,
      followingCount: raw.following_count,
      following: raw.following,
      owner: raw.owner
    };

    setUser(updatedUser);
    setAuthUser(updatedUser);
    localStorage.setItem("user", JSON.stringify(updatedUser));
    setShowProfileEdit(false);
  } catch (e) {
    console.error("프로필 저장 실패", e);
  }
};

  // ✅ 팔로워 목록 조회
  const fetchFollowers = async () => {
    if (!user) return;
    const res = await api.get(
      user.owner ? `/follow/me/followers` : `/follow/public/${user.id}/followers`
    );
    const list: Follow[] = res.data.content.map((f: any) => ({
      id: f.id,
      username: f.name,
      profileImage: f.profile_image,
    }));
    setFollowers(list);
    setShowFollowers(true);
  };

  // ✅ 팔로잉 목록 조회
  const fetchFollowings = async () => {
    if (!user) return;
    const res = await api.get(
      user.owner ? `/follow/me/followings` : `/follow/public/${user.id}/followings`
    );
    const list: Follow[] = res.data.content.map((f: any) => ({
      id: f.id,
      username: f.name,
      profileImage: f.profile_image,
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
          owner={user.owner}
          following={user.following}
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
          onProfileClick={(id) => {
          setShowFollowers(false); // ✅ 추가
          navigate(`/mypage/${id}`);}}
        />

        <FollowListModal
          show={showFollowings}
          onClose={() => setShowFollowings(false)}
          title="팔로잉"
          users={followings}
          onProfileClick={(id) => {
          setShowFollowings(false); // ✅ 추가
          navigate(`/mypage/${id}`);}}
        />

        <ProfileEditorModal
          show={showProfileEdit}
          onClose={() => setShowProfileEdit(false)}
          username={user?.username || ""}
          profileImage={user?.profileImage || ""}
          onSave={handleSaveProfile}
        />
      </div>
      
      <div className="p-4 max-w-3xl mx-auto">
        <h2 className="text-2xl font-bold mb-4">My Posts</h2>
        <InfiniteScroll
              apiEndpoint={`/posts/member/${memberId}`}
              limit={limit}
              fetchKey={`member-${memberId}`} 
              renderPosts={(posts, lastPostRef) => (
                  <CardList posts={posts} lastPostRef={lastPostRef} />
          )}
        />
      </div>
    </div>
  );
}
