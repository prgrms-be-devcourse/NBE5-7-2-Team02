import { Post } from "../types/Post";
import {Avatar, Badge, Card} from "flowbite-react";
import {ImageComponent} from "./ImageComponent";
import {FaHeart, FaRegHeart } from "react-icons/fa";
import { useNavigate } from "react-router-dom";


interface CardItemProps {
  post: Post;
}

export const CardItem = ({ post }: CardItemProps) => {
  const navigate = useNavigate();

  const handleMemberClick = () => {
    navigate(`/mypage?memberId=${post.member_id}`);
  };

  return (
      <Card className="w-full dark:border-gray-600 dark:!bg-dark">
        <div className="flex items-center justify-between mb-2">
          {/* ✅ 작성자 정보 */}
          <div
              className="flex items-center space-x-4 cursor-pointer"
              onClick={handleMemberClick}
          >
            <Avatar img={post.member_image} />
            <p className="truncate text-sm font-medium text-gray-900 dark:text-white">
              {post.member_name}
            </p>
          </div>

          {/* ✅ 좋아요 수 */}
          <div className="flex items-center space-x-1 text-sm text-gray-700 dark:text-gray-400">
            {post.is_like? <FaHeart /> : <FaRegHeart/>}
            <span>{post.num_likes}</span>
          </div>
        </div>
        <div className="flex justify-between items-center">
          {/* ✅ 게시글 제목 */}
          <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            {post.title}
          </h5>

          {/* ✅ 모집 정보 */}
          {post.recruitment_status === "RECRUITING" && (
              <Badge size="sm" color="success">
                모집중
              </Badge>
          )}
          {post.recruitment_status === "DONE" && (
              <Badge size="sm" color="gray">
                모집완료
              </Badge>
          )}
        </div>

        {/* ✅ 게시글 내용 */}
        <p className="font-normal text-gray-700 dark:text-gray-400">
          {post.content}
        </p>
        
        {/* ✅ 이미지 컴포넌트 (이미지가 존재할 경우에만) */}
        {post.images && post.images.length > 0 && (
            <div className="w-full mt-2">
              <ImageComponent images={post.images} className="mb-2" />
            </div>
        )}

        {/* ✅ 태그 */}
        <div className="flex flex-wrap gap-2">
           {post.tags.map((tag, index) => (
               <Badge size="sm" color="gray" key={index}>{tag}</Badge>
           ))}
        </div>
      </Card>
  );
};
