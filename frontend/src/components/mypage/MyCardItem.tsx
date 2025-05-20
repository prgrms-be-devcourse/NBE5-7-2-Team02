import { useState } from "react";
import { Post } from "@/types/Post.ts";
import { Badge, Card, Dropdown } from "flowbite-react";
import { HiOutlineDotsVertical } from "react-icons/hi";
import { useNavigate } from "react-router-dom";
import api from "../../api/axiosInstance";
import { ImageComponent } from "../ImageComponent.tsx";
import { DeleteConfirmModal } from "./DeleteConfirmModal"; // import 경로 조정 필요

interface Props {
  post: Post;
  onDelete: (postId: number) => void;
}

export const MyCardItem = ({ post, onDelete }: Props) => {
  const navigate = useNavigate();
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const handleEdit = () => {
    navigate(`/posts/edit/${post.post_id}`);
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/posts/${post.post_id}`);
      onDelete(post.post_id);
    } catch (err) {
      console.error("삭제 실패:", err);
    } finally {
      setShowDeleteModal(false);
    }
  };

  return (
    <>
      <Card className="w-full relative dark:border-gray-600 dark:!bg-dark">
        <div className="absolute top-4 right-2 z-10">
          <Dropdown
            inline
            renderTrigger={() => (
              <HiOutlineDotsVertical className="text-xl cursor-pointer" />
            )}
          >
            <div className="flex flex-col min-w-[80px] text-sm">
              <button
                onClick={handleEdit}
                className="w-full px-4 py-2 text-left text-gray-700 hover:bg-gray-100"
              >
                수정
              </button>
              <button
                onClick={() => setShowDeleteModal(true)}
                className="w-full px-4 py-2 text-left text-red-600 hover:bg-red-100"
              >
                삭제
              </button>
            </div>
          </Dropdown>
        </div>

        <h5 className="text-xl font-bold text-gray-900 dark:text-white">{post.title}</h5>
        <p className="text-gray-700 dark:text-gray-400">{post.content}</p>

        {post.images?.length > 0 && (
          <div className="w-full mt-2">
            <ImageComponent images={post.images} />
          </div>
        )}

        <div className="flex flex-wrap gap-2 mt-2">
          {post.tags.map((tag, i) => (
            <Badge key={i} size="sm">
              {tag}
            </Badge>
          ))}
        </div>
      </Card>

      <DeleteConfirmModal
        show={showDeleteModal}
        onCancel={() => setShowDeleteModal(false)}
        onConfirm={handleDelete}
      />
    </>
  );
};
