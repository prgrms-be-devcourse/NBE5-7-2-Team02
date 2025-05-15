export interface Post {
  post_id: number;
  title: string;
  content: string;
  created_at: string;
  updated_at: string;
  recruitment_status: string;
  num_like: number;
  chatroom_id: number;
  member_id: number;
  member_name: string;
  member_image: string;
  tags: string[];
}