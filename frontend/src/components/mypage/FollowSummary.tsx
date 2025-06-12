import { Avatar } from "flowbite-react";

interface Props {
  username: string;
  profileImage: string;
  followerCount: number;
  followingCount: number;
  owner: boolean;
  following: boolean;
  onEditProfile: () => void;
  onFollowToggle: () => void;
  onShowFollowers: () => void;
  onShowFollowings: () => void;
}

export default function FollowSummary({
  username,
  profileImage,
  followerCount,
  followingCount,
  owner,
  following,
  onEditProfile,
  onFollowToggle,
  onShowFollowers,
  onShowFollowings,
}: Props) {
  return (
    <div className="text-center space-y-3">
      <div className="flex items-center justify-center gap-4">
        <Avatar img={profileImage} size="xl" />
        <div className="flex flex-col items-start">
          <p className="text-xl font-bold text-dark dark:text-white">{username}</p>

          {/* ✅ 버튼 공통 스타일 분기 처리 */}
          <button
            onClick={owner ? onEditProfile : onFollowToggle}
            className={`mt-1 px-4 py-1 text-sm font-medium rounded-full transition
              ${owner
                ? "bg-white text-black hover:bg-gray-200 border-dark"
                : following
                  ? "bg-transparent border-dark text-dark bg-white hover:bg-dark hover:text-white"
                  : "bg-white border-dark text-black hover:bg-gray-200"
              }`}
          >
            {owner ? "프로필 편집" : following ? "언팔로우" : "팔로우"}
          </button>
        </div>
      </div>

      <div className="flex justify-center gap-8 text-center pt-2">
        <div className="cursor-pointer" onClick={onShowFollowers}>
          <p className="text-lg font-semibold">{followerCount}</p>
          <p className="text-sm text-gray-400">팔로워</p>
        </div>
        <div className="cursor-pointer" onClick={onShowFollowings}>
          <p className="text-lg font-semibold">{followingCount}</p>
          <p className="text-sm text-gray-400">팔로잉</p>
        </div>
      </div>
    </div>
  );
}
