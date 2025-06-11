"use client"

interface ChatRoom {
  id: number
  post_id: number
  title: string
  member_count: number;
  updated_at: Date;
}

interface ChatRoomItemComponentProps {
  chatRoom: ChatRoom
  onRoomSelect: (roomId: number, title: string) => void
  isActive?: boolean
  isCompact?: boolean
}

function ChatRoomItem({ chatRoom, onRoomSelect, isActive = false, isCompact = false }: ChatRoomItemComponentProps) {
  const handleEnterRoom = () => {
    console.log(`Entering chat room: ${chatRoom.id}`)
    onRoomSelect(chatRoom.id, chatRoom.title)
  }

  return (
    <div
      className={`bg-white rounded-lg p-4 mb-2 shadow-sm hover:shadow-md transition-all duration-200 border ${
        isActive ? "border-[#1877f2] bg-[#e7f3ff]" : "border-[#e4e6eb] hover:border-[#dddfe2]"
      }`}
      onClick={handleEnterRoom}
    >
      {isCompact ? (
        // 컴팩트 모드 (채팅방이 선택되었을 때)
        <div className="flex flex-col">
          <div className="flex items-center mb-2">
            <h3 className="text-[15px] font-semibold text-[#050505] truncate">{chatRoom.title}</h3>
          </div>
          <div className="text-[13px] text-[#65676b] mb-2">
            멤버: {chatRoom.member_count}명</div>
            <div className="text-[13px] text-[#65676b] mb-2"> 업데이트: {new Date(chatRoom.updated_at).toLocaleString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit", hour: "2-digit", minute: "2-digit" })}
          </div >
          <button
            className="bg-[#1877f2] hover:bg-[#166fe5] text-white rounded-md py-2 text-sm font-medium transition-colors w-full"
            onClick={(e) => {
              e.stopPropagation()
              handleEnterRoom()
            }}
          >
            채팅방 입장
          </button>
        </div>
      ) : (
        // 기본 모드 (채팅방 목록만 표시될 때)
        <div className="flex justify-between items-center">
          <div className="flex items-center flex-1 min-w-0 mr-3">
            <div className="w-full">
              <h3 className="text-[15px] font-semibold text-[#050505] truncate">{chatRoom.title}</h3>
              <div className="text-[13px] text-[#65676b]">
                멤버: {chatRoom.member_count}명 • 업데이트: {new Date(chatRoom.updated_at).toLocaleString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit", hour: "2-digit", minute: "2-digit" })}
              </div>
            </div>
          </div>
          <button
            className="bg-[#1877f2] hover:bg-[#166fe5] text-white rounded-md px-4 py-2 text-sm font-medium transition-colors flex-shrink-0"
            onClick={(e) => {
              e.stopPropagation()
              handleEnterRoom()
            }}
          >
            채팅방 입장
          </button>
        </div>
      )}
    </div>
  )
}

export default ChatRoomItem
