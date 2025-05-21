export interface Member {
  id: number;
  username: string;
  profileImage: string;
  followerCount: number;
  followingCount: number;
  following: boolean;
  owner: boolean;
}