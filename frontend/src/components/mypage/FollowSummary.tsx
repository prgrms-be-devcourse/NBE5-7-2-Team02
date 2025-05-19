// components/mypage/FollowSummary.tsx
import { Avatar, Button } from "flowbite-react";

interface Props {
  username: string;
  profileImage: string;
  followerCount: number;
  followingCount: number;
  isOwner: boolean;
  isFollowing?: boolean;
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
  isOwner,
  isFollowing,
  onEditProfile,
  onFollowToggle,
  onShowFollowers,
  onShowFollowings,
}: Props) {
  return (
    <div className="text-center space-y-3">
      <Avatar img={profileImage} size="xl" />
      <p className="text-xl font-bold">{username}</p>
      <div className="flex justify-center gap-8 text-center">
        <div className="cursor-pointer" onClick={onShowFollowers}>
          <p className="text-lg font-semibold">{followerCount}</p>
          <p className="text-sm text-gray-400">팔로워</p>
        </div>
        <div className="cursor-pointer" onClick={onShowFollowings}>
          <p className="text-lg font-semibold">{followingCount}</p>
          <p className="text-sm text-gray-400">팔로잉</p>
        </div>
      </div>
      {isOwner ? (
        <Button onClick={onEditProfile}>프로필 편집</Button>
      ) : (
        <Button onClick={onFollowToggle}>
          {isFollowing ? "언팔로우" : "팔로우"}
        </Button>
      )}
    </div>
  );
}
