export interface RecruitmentField {
  field_name: string;
  total_count: number;
  current_count: number;
  closed: boolean;
}

export interface Post {
  post_id: number;
  title: string;
  content: string;
  created_at: string;
  updated_at: string;
  recruitment_status: string;
  recruitment_deadline?: string;
  recruitment_fields?: RecruitmentField[];
  num_likes: number;
  chatroom_id: number;
  member_id: number;
  member_name: string;
  member_image: string;
  tags: string[];
  images: string[];
  is_like: boolean;
}
