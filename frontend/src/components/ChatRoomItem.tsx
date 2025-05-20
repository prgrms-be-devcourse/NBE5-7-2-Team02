"use client"

interface ChatRoom {
  id: number
  post_id: number
  title: string
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
      className={`bg-white rounded-lg p-4 mb-2 shadow-sm hover:shadow-md transition-all duration-200 border ${isActive ? "border-[#1877f2] bg-[#e7f3ff]" : "border-[#e4e6eb] hover:border-[#dddfe2]"
        }`}
      onClick={handleEnterRoom}
    >
      {isCompact ? (
        // 컴팩트 모드 (채팅방이 선택되었을 때)
        <div className="flex flex-col">
          <div className="flex items-center mb-2">
            <div className="w-10 h-10 rounded-full bg-[#e4e6eb] flex items-center justify-center mr-3 text-[#1877f2] font-bold">
              {chatRoom.title.charAt(0).toUpperCase()}
            </div>
            <h3 className="text-[15px] font-semibold text-[#050505] truncate">{chatRoom.title}</h3>
          </div>
          <div className="text-[13px] text-[#65676b] mb-2">
            Room ID: {chatRoom.id} • Post ID: {chatRoom.post_id}
          </div>
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
          <div className="flex items-center">
            <div className="w-10 h-10 rounded-full bg-[#e4e6eb] flex items-center justify-center mr-3 text-[#1877f2] font-bold">
              {chatRoom.title.charAt(0).toUpperCase()}
            </div>
            <div>
              <h3 className="text-[15px] font-semibold text-[#050505]">{chatRoom.title}</h3>
              <div className="text-[13px] text-[#65676b]">
                Room ID: {chatRoom.id} • Post ID: {chatRoom.post_id}
              </div>
            </div>
          </div>
          <button
            className="bg-[#1877f2] hover:bg-[#166fe5] text-white rounded-md px-4 py-2 text-sm font-medium transition-colors"
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
