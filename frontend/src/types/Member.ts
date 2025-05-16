export interface Member {
  id: number;
  username: string;
  profileImage: string;
  followerCount: number;
  followingCount: number;
  isFollowing?: boolean;
  isOwner: boolean;
}