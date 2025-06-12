"use client";

import type { Post } from "../types/Post";
import { Avatar, Badge, Card, Dropdown } from "flowbite-react";
import { ImageComponent } from "./ImageComponent";
import { FaHeart, FaRegHeart } from "react-icons/fa";
import { BsChatDots } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import api from "@/api/axiosInstance";
import { useState } from "react";
import { DeleteConfirmModal } from "./mypage/DeleteConfirmModal";
import { HiOutlineDotsHorizontal } from "react-icons/hi";
import { useAuth } from "../context/AuthContext";
import { AlertModal } from "./AlertModal";

interface CardItemProps {
  post: Post;
}

export const CardItem = ({ post }: CardItemProps) => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showAlertModal, setShowAlertModal] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");

  const [isLiked, setIsLiked] = useState(post.is_like);
  const [likeCount, setLikeCount] = useState(post.num_likes);
  const [recruitmentFields, setRecruitmentFields] = useState(
      post.recruitment_fields || [],
  );

  const handleMemberClick = () => {
    if (!isAuthenticated) {
      setAlertMessage("로그인이 필요합니다.");
      setShowAlertModal(true);
      return;
    }
    navigate(`/mypage/${post.member_id}`);
  };

  const handleEdit = () => {
    navigate(`/posts/edit/${post.post_id}`);
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/posts/${post.post_id}`);
      window.location.reload();
    } catch (err) {
      console.error("삭제 실패:", err);
    } finally {
      setShowDeleteModal(false);
    }
  };

  const handleToggleLike = async () => {
    if (!isAuthenticated) {
      setAlertMessage("로그인이 필요합니다.");
      setShowAlertModal(true);
      return;
    }

    try {
      if (isLiked) {
        await api.delete(`/posts/${post.post_id}/likes`);
        setIsLiked(false);
        setLikeCount((prev) => prev - 1);
      } else {
        await api.post(`/posts/${post.post_id}/likes`);
        setIsLiked(true);
        setLikeCount((prev) => prev + 1);
      }
    } catch (err) {
      console.error("좋아요 실패:", err);
    }
  };

  const handleApplyField = async (fieldName: string) => {
    try {
      await api.post(
          `/posts/${post.post_id}/apply`,
          { fieldName },
          {
            headers: { "Content-Type": "application/json" },
          },
      );

      setRecruitmentFields((prev) =>
          prev.map((field) =>
              field.field_name === fieldName
                  ? { ...field, current_count: field.current_count + 1 }
                  : field,
          ),
      );

      setAlertMessage(`${fieldName} 분야에 지원 완료되었습니다.`);
    } catch (error: any) {
      console.error("지원 실패: ", error);
      const message =
          error?.response?.data?.message ||
          (typeof error?.response?.data === "string"
              ? error.response.data
              : "지원 중 문제가 발생했습니다.");
      setAlertMessage(message);
    } finally {
      setShowAlertModal(true);
    }
  };

  return (
      <>
        <Card className="dark:!bg-dark relative w-full dark:border-gray-600">
          {/* 상단 버튼들 */}
          <div className="absolute top-4 right-2 z-10 flex items-center space-x-3">
            <div className="flex items-center space-x-1">
              {isAuthenticated ? (
                  isLiked ? (
                      <FaHeart
                          className="cursor-pointer text-red-500"
                          onClick={handleToggleLike}
                      />
                  ) : (
                      <FaRegHeart
                          className="cursor-pointer"
                          onClick={handleToggleLike}
                      />
                  )
              ) : (
                  <FaRegHeart
                      className="cursor-pointer"
                      onClick={() => {
                        setAlertMessage("로그인이 필요합니다.");
                        setShowAlertModal(true);
                      }}
                  />
              )}
              <span>{likeCount}</span>
            </div>

            <BsChatDots
                className="cursor-pointer text-lg hover:text-gray-900 dark:hover:text-white"
                title="채팅 참여"
                onClick={() => {
                  setAlertMessage("채팅은 별도 구현 필요");
                  setShowAlertModal(true);
                }}
            />

            {user?.id === post.member_id && (
                <Dropdown
                    inline
                    renderTrigger={() => (
                        <HiOutlineDotsHorizontal className="cursor-pointer text-xl" />
                    )}
                >
                  <button
                      onClick={handleEdit}
                      className="w-full px-4 py-2 text-left text-gray-700 hover:bg-gray-100 dark:text-white dark:hover:bg-gray-500"
                  >
                    수정
                  </button>
                  <button
                      onClick={() => setShowDeleteModal(true)}
                      className="w-full px-4 py-2 text-left text-red-600 hover:bg-red-100 dark:hover:bg-red-200"
                  >
                    삭제
                  </button>
                </Dropdown>
            )}
          </div>

          {/* 작성자 정보 */}
          <div className="mt-2 mb-2 flex items-center justify-between">
            <div
                className="flex w-full cursor-pointer items-center space-x-4"
                onClick={handleMemberClick}
            >
              <Avatar img={post.member_image} />
              <p className="truncate text-sm font-medium text-gray-900 dark:text-white">
                {post.member_name}
              </p>
            </div>
          </div>

          {/* 제목 & 상태 */}
          <div className="flex items-center justify-between">
            <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
              {post.title}
            </h5>
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

          {/* 내용 */}
          <p className="font-normal text-gray-700 dark:text-gray-400">
            {post.content}
          </p>

          {/* 이미지 */}
          {post.images?.length > 0 && (
              <div className="mt-2 w-full">
                <ImageComponent images={post.images} className="mb-2" />
              </div>
          )}

          {/* 마감일 & 모집 분야 */}
          {post.recruitment_status !== "NONE" && (
              <div className="mt-3 text-base text-gray-700 dark:text-gray-300">
                {post.recruitment_deadline && (
                    <p>
                      <span className="font-semibold">마감일 : </span>
                      {new Intl.DateTimeFormat("ko-KR", {
                        year: "numeric",
                        month: "numeric",
                        day: "numeric",
                      })
                      .format(new Date(post.recruitment_deadline))
                      .replace(/\.$/, "")}
                    </p>
                )}
                {recruitmentFields.length > 0 && (
                    <div className="mt-2">
                      <p className="font-semibold">모집 분야</p>
                      <ul className="list-inside list-disc space-y-2 pl-4">
                        {recruitmentFields.map((field, idx) => {
                          const isClosed =
                              field.closed || field.current_count >= field.total_count;
                          return (
                              <li
                                  key={idx}
                                  className="flex items-center justify-between pl-1 before:mr-2 before:text-gray-500 before:content-['•']"
                              >
                        <span className="flex-1">
                          {field.field_name} - {field.current_count} /{" "}
                          {field.total_count}
                        </span>
                                {isAuthenticated && user?.id !== post.member_id && (
                                    <button
                                        className={`ml-4 rounded px-3 py-1 text-sm ${
                                            isClosed
                                                ? "cursor-not-allowed bg-gray-400 text-white"
                                                : "!bg-blue-800 text-white hover:!bg-blue-800"
                                        }`}
                                        disabled={isClosed}
                                        onClick={() => handleApplyField(field.field_name)}
                                    >
                                      {isClosed ? "모집 마감" : "지원하기"}
                                    </button>
                                )}
                                {!isAuthenticated && isClosed && (
                                    <span className="ml-4 cursor-default rounded bg-gray-400 px-3 py-1 text-sm text-white">
                            모집 마감
                          </span>
                                )}
                              </li>
                          );
                        })}
                      </ul>
                    </div>
                )}
              </div>
          )}

          {/* 태그 */}
          <div className="flex flex-wrap gap-2">
            {post.tags.map((tag, index) => (
                <Badge size="sm" color="gray" key={index}>
                  {tag}
                </Badge>
            ))}
          </div>
        </Card>

        {/* 삭제 모달 */}
        <DeleteConfirmModal
            show={showDeleteModal}
            onCancel={() => setShowDeleteModal(false)}
            onConfirm={handleDelete}
        />

        {/* 알림 모달 */}
        <AlertModal
            show={showAlertModal}
            onClose={() => setShowAlertModal(false)}
            message={alertMessage}
        />
      </>
  );
};
