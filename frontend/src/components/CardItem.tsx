"use client"

import type { Post } from "../types/Post"
import { Avatar, Badge, Card, Dropdown } from "flowbite-react"
import { ImageComponent } from "./ImageComponent"
import { FaHeart, FaRegHeart } from "react-icons/fa"
import { BsChatDots } from "react-icons/bs" // Import chat icon
import { useNavigate } from "react-router-dom"
import api from "@/api/axiosInstance"
import { useState } from "react"
import { DeleteConfirmModal } from "./mypage/DeleteConfirmModal"
import { HiOutlineDotsHorizontal } from "react-icons/hi"
import { useAuth } from "../context/AuthContext"
import { toast } from "react-toastify"

interface CardItemProps {
  post: Post
}

export const CardItem = ({ post }: CardItemProps) => {
  const navigate = useNavigate()
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const { user, isAuthenticated } = useAuth()
  const [chatRoomId, setchatRoomId] = useState()

  const [isLiked, setIsLiked] = useState(post.is_like)
  const [likeCount, setLikeCount] = useState(post.num_likes)

  const handleMemberClick = () => {
    if (!isAuthenticated) {
      toast.info("로그인이 필요합니다.")
      return
    }
    navigate(`/mypage?memberId=${post.member_id}`)
  }

  const handleEdit = () => {
    navigate(`/posts/edit/${post.post_id}`)
  }

  const onDelete = () => {
    window.location.reload()
  }

  const handleDelete = async () => {
    try {
      await api.delete(`/posts/${post.post_id}`)
      onDelete()
    } catch (err) {
      console.error("삭제 실패:", err)
    } finally {
      setShowDeleteModal(false)
    }
  }

  const handleChatJoin = async () => {
    if (!isAuthenticated) {
      alert("채팅에 참여하려면 로그인이 필요합니다.")
      return
    }

    let resRoomId // 최종적으로 사용될 채팅방 ID

    try {
      // 1. 기존 채팅방 조회
      console.log(`[1] 기존 채팅방 조회 시도: /api/chatroom/${post.post_id}`)
      const getChatroomResponse = await fetch(`http://localhost:8080/api/chatroom/${post.post_id}`)
      console.log(`[1] 기존 채팅방 조회 응답 상태: ${getChatroomResponse.status}`)

      if (getChatroomResponse.ok) {
        // status 200-299
        const res = await getChatroomResponse.json()
        if (res.data && res.data.id) {
          resRoomId = res.data.id
          console.log(`[1] 기존 채팅방 ID (${resRoomId}) 발견`)
          setchatRoomId(resRoomId) // 상태 업데이트
        } else {
          // 200 응답이지만, 예상치 못한 응답 구조
          console.error("채팅방 조회 성공했으나, 응답 데이터에 ID가 없습니다:", res)
          alert("채팅방 정보를 가져오는 중 오류가 발생했습니다. (데이터 형식 오류)")
          return
        }
      } else if (getChatroomResponse.status === 404) {
        console.log(`[1] 기존 채팅방 없음 (404). 새로 생성 시도: /chatroom/${post.post_id}`)
        // 2. 채팅방이 없으면 생성
        // api.post가 에러를 throw하지 않고 응답 객체를 반환한다고 가정
        const createResponse = await api.post(`/chatroom/${post.post_id}`)

        // createResponse.status는 api.post가 axios 인스턴스라면 응답 객체 내에 있을 것입니다.
        // createResponse가 바로 데이터일 수도 있습니다. api 래퍼의 구현에 따라 다릅니다.
        // 여기서는 createResponse가 axios 응답 객체와 유사하다고 가정합니다.
        console.log(`[2] 채팅방 생성 시도 응답 상태: ${createResponse.status}`) // api.post가 반환하는 객체에 status가 있다고 가정

        // 생성 성공 후, 다시 GET 요청으로 ID를 가져옵니다.
        // (생성 요청의 응답 본문에 ID가 바로 포함되어 있다면 이 GET은 생략 가능)
        if (createResponse.status === 201 || createResponse.status === 200) {
          // 201 Created 또는 200 OK (이미 존재해서 가져온 경우 등)
          console.log(`[2a] 채팅방 생성 성공 또는 이미 존재. 다시 정보 조회: /api/chatroom/${post.post_id}`)
          const getNewChatroomResponse = await fetch(`http://localhost:8080/api/chatroom/${post.post_id}`)
          console.log(`[2a] 생성 후 채팅방 조회 응답 상태: ${getNewChatroomResponse.status}`)

          if (getNewChatroomResponse.ok) {
            const newRes = await getNewChatroomResponse.json()
            if (newRes.data && newRes.data.id) {
              resRoomId = newRes.data.id
              // 원본 코드에서는 setchatRoomId(res.data.data) 였는데,
              // 일관성을 위해 newRes.data.id로 가정합니다.
              // 만약 API가 정말로 { data: { data: 'id_value' } } 형태로 반환한다면 newRes.data.data가 맞습니다.
              console.log(`[2a] 새로 생성/조회된 채팅방 ID (${resRoomId}) 확인`)
              setchatRoomId(resRoomId) // 상태 업데이트
            } else {
              console.error("채팅방 생성 후 조회했으나, 응답 데이터에 ID가 없습니다:", newRes)
              alert("채팅방 생성 후 정보를 가져오는 중 오류가 발생했습니다. (데이터 형식 오류)")
              return
            }
          } else {
            // 생성 후 GET 실패
            console.error(`채팅방 생성 후 조회 실패. 상태: ${getNewChatroomResponse.status}`)
            alert("채팅방 생성 후 정보를 가져오는 중 오류가 발생했습니다.")
            return
          }
        } else {
          // 채팅방 생성 요청 실패
          console.error(`채팅방 생성 요청 실패. 상태: ${createResponse.status}`, createResponse.data) // createResponse.data는 에러 메시지일 수 있음
          alert("채팅방 생성 중 오류가 발생했습니다.")
          return
        }
      } else {
        // 200도 404도 아닌 다른 HTTP 에러
        const errorText = await getChatroomResponse.text()
        console.error(`채팅방 조회 중 예상치 못한 HTTP 상태 코드: ${getChatroomResponse.status}`, errorText)
        alert(`채팅방 정보를 가져오는 중 오류가 발생했습니다. (상태: ${getChatroomResponse.status})`)
        return
      }

      // 3. resRoomId가 성공적으로 할당되었는지 확인
      if (!resRoomId) {
        console.error("최종적으로 resRoomId를 가져오지 못했습니다.")
        alert("채팅방 ID를 가져오지 못했습니다. 다시 시도해주세요.")
        return
      }

      console.log(`[3] 채팅방 ID (${resRoomId})로 멤버 추가 시도: /chatroom/${resRoomId}/member`)
      // 4. 채팅방에 멤버로 참여
      // api.post의 반환값을 resRoomId에 다시 할당하는 것은 의도된 것인지 확인 필요.
      // 보통 멤버 추가 API는 성공 여부나 멤버 정보를 반환하지, 채팅방 ID를 다시 반환하지 않습니다.
      // 여기서는 원본 코드의 로직을 따르되, 주석으로 의문을 남깁니다.
      const addMemberResponse = await api.post(`/chatroom/${resRoomId}/member`)
      console.log("[3] 멤버 추가 응답:", addMemberResponse) // addMemberResponse의 전체 구조 확인

      // 만약 addMemberResponse가 채팅방 ID를 포함하는 새로운 객체를 반환하고
      // 그것을 resRoomId에 재할당해야 한다면, 해당 로직을 명확히 해야 합니다.
      // 예를 들어, addMemberResponse.data.roomId 와 같은 형태일 수 있습니다.
      // 지금은 원본처럼 addMemberResponse 자체를 할당합니다.
      // resRoomId = addMemberResponse; // 이 줄이 원래 의도였다면, addMemberResponse의 내용을 확인해야 합니다.

      console.log(`[4] 채팅방 목록으로 이동`)
      navigate("/ChatRoomList")
    } catch (error) {
      // 네트워크 에러, JSON 파싱 에러 등
      console.error("채팅방 참여 처리 중 전체 오류 발생:", error)
      let errorMessage = "채팅방 처리 중 오류가 발생했습니다."
      if (error.response && error.response.data && error.response.data.message) {
        // axios 에러의 경우
        errorMessage = error.response.data.message
      } else if (error.message) {
        errorMessage = error.message
      }
      alert(errorMessage)
    }
  }

  const handleToggleLike = async () => {
    if (!isAuthenticated) {
      toast.info("로그인이 필요합니다.")
      return
    }

    try {
      if (isLiked) {
        await api.delete(`/posts/${post.post_id}/likes`)
        setIsLiked(false)
        setLikeCount((prev) => prev - 1)
      } else {
        await api.post(`/posts/${post.post_id}/likes`)
        setIsLiked(true)
        setLikeCount((prev) => prev + 1)
      }
    } catch (err) {
      console.error("좋아요 처리 실패:", err)
    }
  }

  return (
    <>
      <Card className="w-full relative dark:border-gray-600 dark:!bg-dark">
        <div className="absolute top-4 right-2 z-10 flex items-center space-x-3">
          {/* Like Button */}
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

          {/* Chat Button */}
          <BsChatDots
            className="text-lg cursor-pointer hover:text-gray-900 dark:hover:text-white"
            onClick={handleChatJoin}
            title="채팅 참여"
          />

          {/* Menu Dropdown (only for post owner) */}
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
  )
}
