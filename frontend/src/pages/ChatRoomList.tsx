"use client"

import { useState, useEffect } from "react";
import ChatRoomItem from "../components/ChatRoomItem";
import ChatRoom from "../components/ChatRoom";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";

// 2a. 서버 응답 객체의 인터페이스 (스네이크 케이스)
interface ServerChatRoom {
  id: number;
  post_id: number; // 서버에서 오는 필드명
  title: string;
}

function ChatRoomList() {
  const [items, setItems] = useState<ServerChatRoom[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [retryCount, setRetryCount] = useState(0);
  const [selectedRoom, setSelectedRoom] = useState<number | null>(null);
  const [selectedRoomTitle, setSelectedRoomTitle] = useState("");


  const fetchData = async () => {
    setLoading(true);
    setError(null);
    const token = localStorage.getItem("accessToken"); // 토큰 가져오기

    if (!token) {
      console.error("[fetchData] Authorization token not found in localStorage.");
      setError("로그인이 필요합니다. 로그인 후 다시 시도해주세요.");
      setLoading(false);
      setItems([]);
      // 여기서 로그인 페이지로 리디렉션하거나 사용자에게 명확한 안내를 할 수 있습니다.
      // 예: router.push('/login'); (Next.js 사용 시)
      return;
    }

    try {
      const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/chatroom/entered`, {
        cache: "no-store",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`, // Authorization 헤더 추가
        },
      });

      if (!res.ok) {
        if (res.status === 401 || res.status === 403) {
          const errorText = await res.text(); // 에러 본문을 읽어옴 (JSON 형태일 수도 있음)
          console.error(`[fetchData] Authentication error (${res.status}):`, errorText);
          setError(`인증에 실패했습니다 (${res.status}). 세션이 만료되었거나 권한이 없습니다. 다시 로그인해주세요.`);
          // 필요하다면 여기서 로그아웃 처리 또는 로그인 페이지로 강제 이동
        } else {
          // 기타 HTTP 에러
          const errorText = await res.text();
          console.error(`[fetchData] HTTP error! status: ${res.status}, message: ${errorText}`);
          throw new Error(`HTTP error! status: ${res.status}, message: ${errorText}`);
        }
        setItems([]);
        setLoading(false);
        return;
      }

      const responseData: ServerChatRoom[] = await res.json();

      if (responseData && Array.isArray(responseData)) {
        setItems(responseData);
      } else {
        setItems([]);
        console.warn("[fetchData] API 응답에서 data 필드를 찾을 수 없거나 형식이 배열이 아닙니다.", responseData);
        setError("채팅방 목록을 가져오는 데 실패했습니다. (응답 데이터 형식 오류)");
      }
    } catch (e: any) {
      console.error("[fetchData] 채팅방 목록을 가져오는 중 오류 발생: ", e);
      // 네트워크 에러 등의 경우 e.message가 있을 수 있음
      setError(e.message || "알 수 없는 오류가 발생했습니다. 네트워크 연결을 확인해주세요.");
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [retryCount]); // retryCount 변경 시 fetchData 재호출

  const handleRetry = () => {
    setRetryCount((prev) => prev + 1);
  };

  const handleRoomSelect = (roomId: number, title: string) => {
    setSelectedRoom(roomId);
    setSelectedRoomTitle(title);
  };

  const handleCloseRoom = () => {
    setSelectedRoom(null);
    setSelectedRoomTitle("");
    fetchData();
  };

  return (
    <div className="flex h-[calc(100vh-4rem)] bg-[#f0f2f5] overflow-hidden p-4">
      <div className={`transition-all duration-300 ease-in-out ${selectedRoom ? "w-1/3 md:w-1/4 lg:w-1/5" : "w-full"}`}>
        <div className="h-full flex flex-col max-w-[480px] mx-auto">
          <div className="flex-grow overflow-hidden rounded-xl bg-white shadow-lg flex flex-col">
            <div className="flex flex-col xl:flex-row xl:items-center justify-between p-4 border-b border-[#e4e6eb]">
              <h1 className="text-xl font-bold text-[#1877f2] mb-2 xl:mb-0">참여중인 채팅방 목록</h1>
              <Button
                variant="outline"
                onClick={handleRetry}
                disabled={loading}
                className="flex items-center gap-2 text-[#1877f2] border-[#e4e6eb] hover:bg-[#f0f2f5] w-full xl:w-auto"
              >
                <RefreshIcon className="h-4 w-4" />
                새로고침
              </Button>
            </div>

            <div className="flex-grow overflow-y-auto p-4">
              {loading ? (
                <div className="space-y-4">
                  <ChatRoomSkeleton />
                  <ChatRoomSkeleton />
                  <ChatRoomSkeleton />
                </div>
              ) : error ? (
                <div className="bg-red-50 border border-red-200 rounded-md p-4 mb-4">
                  <div className="flex items-start">
                    <AlertCircleIcon className="h-5 w-5 text-red-500 mr-2 mt-0.5" />
                    <div>
                      <h3 className="text-sm font-medium text-red-800">오류가 발생했습니다</h3>
                      <p className="text-sm text-red-700 mt-1">{error}</p>
                      <Button variant="outline" size="sm" onClick={handleRetry} className="mt-2">
                        다시 시도
                      </Button>
                    </div>
                  </div>
                </div>
              ) : items.length > 0 ? (
                <div className="space-y-3">
                  {items.map((chatRoom) => (
                    <ChatRoomItem
                      key={chatRoom.id}
                      chatRoom={chatRoom}
                      onRoomSelect={handleRoomSelect}
                      isActive={selectedRoom === chatRoom.id}
                      isCompact={selectedRoom !== null}
                    />
                  ))}
                </div>
              ) : (
                <div className="text-center py-12 bg-[#f0f2f5] rounded-lg">
                  <ChatBubbleIcon className="h-12 w-12 text-[#65676b] mx-auto mb-4" />
                  <p className="text-[#65676b] mb-4">현재 참여중인 채팅방이 없습니다.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      <div
        className={`transition-all duration-300 ease-in-out ${selectedRoom ? "w-2/3 md:w-3/4 lg:w-4/5 opacity-100" : "w-0 opacity-0"
          } h-full flex items-center justify-center`}
      >
        {selectedRoom && (
          <div className="w-[500px] h-[95%] rounded-xl overflow-hidden shadow-lg bg-white">
            <ChatRoom chatRoomId={selectedRoom.toString()} postTitle={selectedRoomTitle} onBack={handleCloseRoom} />
          </div>
        )}
      </div>
    </div >
  );
}

function ChatRoomSkeleton() {
  return (
    <div className="border border-[#e4e6eb] rounded-lg p-4 animate-pulse bg-white shadow-sm">
      <div className="flex justify-between items-center">
        <Skeleton className="h-6 w-48" />
        <Skeleton className="h-9 w-24" />
      </div>
      <Skeleton className="h-4 w-32 mt-2" />
    </div>
  );
}

function RefreshIcon(props: React.SVGProps<SVGSVGElement>) { // props 타입 추가
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      {...props}
    >
      <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8" />
      <path d="M21 3v5h-5" />
      <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16" />
      <path d="M3 21v-5h5" />
    </svg>
  );
}

function AlertCircleIcon(props: React.SVGProps<SVGSVGElement>) { // props 타입 추가
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      {...props}
    >
      <circle cx="12" cy="12" r="10" />
      <line x1="12" y1="8" x2="12" y2="12" />
      <line x1="12" y1="16" x2="12.01" y2="16" />
    </svg>
  );
}

function ChatBubbleIcon(props: React.SVGProps<SVGSVGElement>) { // props 타입 추가
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      {...props}
    >
      <path d="M14 9a2 2 0 0 1-2 2H6l-4 4V4c0-1.1.9-2 2-2h8a2 2 0 0 1 2 2v5Z" />
      <path d="M18 9h2a2 2 0 0 1 2 2v11l-4-4h-6a2 2 0 0 1-2-2v-1" />
    </svg>
  );
}

export default ChatRoomList;