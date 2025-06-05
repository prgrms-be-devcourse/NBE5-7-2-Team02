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
import { toast } from "react-toastify";

interface CardItemProps {
  post: Post;
}

export const CardItem = ({ post }: CardItemProps) => {
  const navigate = useNavigate();
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const { user, isAuthenticated } = useAuth();
  const [chatRoomId, setchatRoomId] = useState<string | undefined>(); // chatRoomId 타입 명시

  const [isLiked, setIsLiked] = useState(post.is_like);
  const [likeCount, setLikeCount] = useState(post.num_likes);

  const handleMemberClick = () => {
    if (!isAuthenticated) {
      toast.info("로그인이 필요합니다.");
      return;
    }
    navigate(`/mypage?memberId=${post.member_id}`);
  };

  const handleEdit = () => {
    navigate(`/posts/edit/${post.post_id}`);
  };

  const onDelete = () => {
    window.location.reload();
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/posts/${post.post_id}`);
      onDelete();
    } catch (err) {
      console.error("삭제 실패:", err);
    } finally {
      setShowDeleteModal(false);
    }
  };

  const handleChatJoin = async () => {
    if (!isAuthenticated) {
      alert("채팅에 참여하려면 로그인이 필요합니다.");
      return;
    }

    let resRoomId;

    try {
      const getChatroomResponse = await fetch(`http://localhost:8080/api/chatroom/${post.post_id}`);

      if (getChatroomResponse.ok) {
        const res = await getChatroomResponse.json();
        if (res.data && res.data.id) {
          resRoomId = res.data.id;
          setchatRoomId(resRoomId);
        } else {
          console.error("채팅방 조회 성공했으나, 응답 데이터에 ID가 없습니다:", res);
          alert("채팅방 정보를 가져오는 중 오류가 발생했습니다. (데이터 형식 오류)");
          return;
        }
      } else if (getChatroomResponse.status === 404) {
        const createResponse = await api.post(`/chatroom/${post.post_id}`);

        if (createResponse.status === 201 || createResponse.status === 200) {
          const getNewChatroomResponse = await fetch(`http://localhost:8080/api/chatroom/${post.post_id}`);

          if (getNewChatroomResponse.ok) {
            const newRes = await getNewChatroomResponse.json();
            if (newRes.data && newRes.data.id) {
              resRoomId = newRes.data.id;
              setchatRoomId(resRoomId);
            } else {
              console.error("채팅방 생성 후 조회했으나, 응답 데이터에 ID가 없습니다:", newRes);
              alert("채팅방 생성 후 정보를 가져오는 중 오류가 발생했습니다. (데이터 형식 오류)");
              return;
            }
          } else {
            console.error(`채팅방 생성 후 조회 실패. 상태: ${getNewChatroomResponse.status}`);
            alert("채팅방 생성 후 정보를 가져오는 중 오류가 발생했습니다.");
            return;
          }
        } else {
          console.error(`채팅방 생성 요청 실패. 상태: ${createResponse.status}`, createResponse.data);
          alert("채팅방 생성 중 오류가 발생했습니다.");
          return;
        }
      } else {
        const errorText = await getChatroomResponse.text();
        console.error(`채팅방 조회 중 예상치 못한 HTTP 상태 코드: ${getChatroomResponse.status}`, errorText);
        alert(`채팅방 정보를 가져오는 중 오류가 발생했습니다. (상태: ${getChatroomResponse.status})`);
        return;
      }

      if (!resRoomId) {
        console.error("최종적으로 resRoomId를 가져오지 못했습니다.");
        alert("채팅방 ID를 가져오지 못했습니다. 다시 시도해주세요.");
        return;
      }

      await api.post(`/chatroom/${resRoomId}/member`);
      navigate("/ChatRoomList");
    } catch (error: any) { // error 타입 명시
      console.error("채팅방 참여 처리 중 전체 오류 발생:", error);
      navigate(`/ChatRoomList`)
    }
  };

  const handleToggleLike = async () => {
    if (!isAuthenticated) {
      toast.info("로그인이 필요합니다.");
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
      console.error("좋아요 처리 실패:", err);
    }
  };

  return (
    <>
      <Card className="w-full relative dark:border-gray-600 dark:!bg-dark">
        <div className="absolute top-4 right-2 z-10 flex items-center space-x-3">
          <div className="flex items-center space-x-1">
            {isAuthenticated ? (
              isLiked ? (
                <FaHeart className="text-red-500 cursor-pointer" onClick={handleToggleLike} />
              ) : (
                <FaRegHeart className="cursor-pointer" onClick={handleToggleLike} />
              )
            ) : isLiked ? (
              <FaHeart className="text-red-500 cursor-pointer" onClick={() => toast.info("로그인이 필요합니다.")} />
            ) : (
              <FaRegHeart className="cursor-pointer" onClick={() => toast.info("로그인이 필요합니다.")} />
            )}
            <span>{likeCount}</span>
          </div>

          <BsChatDots
            className="text-lg cursor-pointer hover:text-gray-900 dark:hover:text-white"
            onClick={handleChatJoin}
            title="채팅 참여"
          />

          {user?.id === post.member_id && (
            <Dropdown inline renderTrigger={() => <HiOutlineDotsHorizontal className="text-xl cursor-pointer" />}>
              <div className="flex flex-col min-w-[80px] text-sm">
                <button onClick={handleEdit} className="w-full px-4 py-2 text-left text-gray-700 hover:bg-gray-100">
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
          )}
        </div>

        <div className="flex items-center justify-between mb-2 mt-2">
          <div className={`flex items-center space-x-4 cursor-pointer w-full`} onClick={handleMemberClick}>
            <Avatar img={post.member_image} />
            <p className="truncate text-sm font-medium text-gray-900 dark:text-white">{post.member_name}</p>
          </div>
        </div>

        <div className="flex justify-between items-center">
          <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">{post.title}</h5>

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

        <p className="font-normal text-gray-700 dark:text-gray-400">{post.content}</p>

        {post.images && post.images.length > 0 && (
          <div className="w-full mt-2">
            <ImageComponent images={post.images} className="mb-2" />
          </div>
        )}

        <div className="flex flex-wrap gap-2">
          {post.tags.map((tag, index) => (
            <Badge size="sm" color="gray" key={index}>
              {tag}
            </Badge>
          ))}
        </div>
      </Card>

      <DeleteConfirmModal show={showDeleteModal} onCancel={() => setShowDeleteModal(false)} onConfirm={handleDelete} />
    </>
  );
};