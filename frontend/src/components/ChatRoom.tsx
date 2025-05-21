"use client"

import type React from "react"

import { useState, useEffect, useRef } from "react"
import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs"
import SockJS from "sockjs-client"

interface ChatRoomProps {
    chatRoomId: string
    postTitle: string
    onBack: () => void // 뒤로가기 버튼 클릭 시 호출될 함수
}

interface ChatRoomInfo {
    id: string
    title: string
}

interface ChatRes {
    id: number
    chatMemberId: number
    chatMemberName: string
    content: string
    createdAt: Date | string
}

interface ServerChatItem {
    id: number
    chat_member_id: number
    chat_member_name: string
    content: string
    created_at: string
}

function ChatRoom({ chatRoomId, postTitle, onBack }: ChatRoomProps) {
    const [items, setItems] = useState<ChatRes[]>([])
    const [loading, setLoading] = useState<boolean>(true)
    const [error, setError] = useState<string | null>(null)
    const [newMessage, setNewMessage] = useState<string>("")
    const [chatRoomInfo, setChatRoomInfo] = useState<ChatRoomInfo | null>(null)

    const stompClientRef = useRef<Client | null>(null)
    const subscriptionRef = useRef<StompSubscription | null>(null)
    const messagesEndRef = useRef<HTMLDivElement>(null)

    const currentMemberId = 1

    console.log("====================postTitle")
    console.log(postTitle);

    // 메시지 목록이 업데이트될 때마다 스크롤을 아래로 이동
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
    }

    useEffect(() => {
        scrollToBottom()
    }, [items])

    useEffect(() => {
        const fetchData = async () => {
            if (!chatRoomId) return
            setLoading(true)
            setError(null)
            console.log(`[fetchData] Fetching initial messages for chatRoomId: ${chatRoomId}`)

            try {
                const response = await fetch(`http://localhost:8080/api/chatroom/${chatRoomId}/message`)

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`)
                }

                // 서버 응답이 { data: ServerChatItem[] } 구조라고 가정
                const resultContainer = await response.json()
                console.log("[fetchData] Raw container from API:", resultContainer)

                const resultFromServer: ServerChatItem[] = resultContainer.data // 실제 데이터 배열 추출

                if (Array.isArray(resultFromServer)) {
                    const mappedResult: ChatRes[] = resultFromServer.map((item) => ({
                        id: item.id,
                        chatMemberId: item.chat_member_id,
                        chatMemberName: item.chat_member_name, // chat_member_name 매핑
                        content: item.content,
                        createdAt: new Date(item.created_at),
                    }))
                    console.log("[fetchData] Mapped data for initial setItems:", mappedResult)
                    setItems(mappedResult)
                } else {
                    console.error(
                        "[fetchData] Extracted data from API is not an array. Setting items to empty array. Received container:",
                        resultContainer,
                    )
                    setItems([])
                }
            } catch (e: any) {
                setError(e.message)
                console.error("[fetchData] Failed to fetch data:", e)
                setItems([])
            } finally {
                setLoading(false)
                console.log("[fetchData] Finished fetching.")
            }
        }

        fetchData()
    }, [chatRoomId])

    useEffect(() => {
        const fetchChatRoomInfo = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/chatroom/${chatRoomId}`)
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`)
                }
                const data = await response.json()
                setChatRoomInfo(data.data)
            } catch (error) {
                console.error("채팅방 정보를 가져오는데 실패했습니다:", error)
            }
        }

        fetchChatRoomInfo()
    }, [chatRoomId])

    useEffect(() => {
        if (!chatRoomId) {
            if (stompClientRef.current && stompClientRef.current.active) {
                stompClientRef.current.deactivate()
            }
            if (subscriptionRef.current) {
                subscriptionRef.current.unsubscribe()
            }
            stompClientRef.current = null
            subscriptionRef.current = null
            return
        }

        console.log(`[STOMP useEffect] Setting up STOMP for chatRoomId: ${chatRoomId}`)
        const token = localStorage.getItem("accessToken");
        console.log("===================");
        console.log(token);

        const client = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/ws/chatroom"),
            debug: (str) => {
                console.log("STOMP DEBUG: " + str)
            },
            reconnectDelay: 5000,

            connectHeaders: {
                Authorization: `Bearer ${token}`,  // 토큰 헤더 추가
            },
        })

        client.onConnect = (frame) => {
            console.log("[STOMP] Connected to server:", frame)
            stompClientRef.current = client

            const topic = `/sub/${chatRoomId}/message`
            console.log(`[STOMP] Subscribing to topic: ${topic}`)
            subscriptionRef.current = client.subscribe(topic, (message: IMessage) => {
                console.log("[STOMP] Raw message body received:", message.body)
                try {
                    const parsedMessage = JSON.parse(message.body)
                    console.log("[STOMP] Parsed JSON from message:", parsedMessage)

                    // 서버 응답이 { body: { data: ServerChatItem } } 구조라고 가정
                    if (parsedMessage && parsedMessage.body && parsedMessage.body.data) {
                        const serverData: ServerChatItem = parsedMessage.body.data
                        console.log("[STOMP] Extracted data from server response (serverData):", serverData)

                        const mappedData: ChatRes = {
                            id: serverData.id,
                            chatMemberId: serverData.chat_member_id,
                            chatMemberName: serverData.chat_member_name, // chat_member_name 매핑
                            content: serverData.content,
                            createdAt: new Date(serverData.created_at),
                        }
                        console.log("[STOMP] Mapped chat data for state update:", mappedData)

                        setItems((prevItems) => {
                            if (!Array.isArray(prevItems)) {
                                return [mappedData]
                            }
                            // 메시지 ID 기반 중복 추가 방지 (StrictMode가 꺼져있으면 덜 중요하지만, 안전장치로 좋음)
                            const exists = prevItems.some((item) => item.id === mappedData.id)
                            if (exists) {
                                console.warn(`[STOMP setItems] Duplicate message ID ${mappedData.id} detected. Not adding.`)
                                return prevItems
                            }
                            return [...prevItems, mappedData]
                        })
                    } else {
                        console.warn(
                            "[STOMP] Received JSON does not have the expected structure (parsedMessage.body.data). Actual JSON:",
                            parsedMessage,
                        )
                    }
                } catch (errorInCallback) {
                    console.error(
                        "[STOMP] Error processing received message or in subscribe callback:",
                        errorInCallback,
                        "Raw message body:",
                        message.body,
                    )
                }
            })
        }

        client.onStompError = (frame) => {
            console.error("[STOMP] Broker reported error:", frame.headers["message"], "Additional details:", frame.body)
        }

        client.onWebSocketError = (event) => {
            console.error("[STOMP] WebSocket error event:", event)
        }

        client.onDisconnect = (frame) => {
            console.log("[STOMP] Disconnected from server:", frame)
        }

        client.activate()

        return () => {
            console.log(`[STOMP Cleanup] Cleaning up STOMP for chatRoomId: ${chatRoomId}`)
            if (subscriptionRef.current) {
                subscriptionRef.current.unsubscribe()
                subscriptionRef.current = null
            }
            if (stompClientRef.current && stompClientRef.current.active) {
                // 또는 .connected
                stompClientRef.current.deactivate()
                stompClientRef.current = null
            }
        }
    }, [chatRoomId])

    const handleMessageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewMessage(event.target.value)
    }

    const handleSendMessage = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()
        if (!newMessage.trim() || !chatRoomId) return

        if (!stompClientRef.current || !stompClientRef.current.connected) {
            console.warn("[SendMessage] STOMP client not connected.")
            return
        }

        const messageToSend = {
            memberId: currentMemberId, // 이 ID는 서버에서 chat_member_id로 사용될 것임
            content: newMessage,
            // chatMemberName은 클라이언트에서 보내는 것이 아니라,
            // 보통 서버에서 memberId를 기반으로 조회하여 응답 메시지에 포함시킴.
        }

        const destination = `/pub/${chatRoomId}/message`

        try {
            stompClientRef.current.publish({
                destination: destination,
                body: JSON.stringify(messageToSend),
            })
            setNewMessage("")
        } catch (error) {
            console.error("[SendMessage] Failed to send message via STOMP:", error)
        }
    }

    return (
        <div className="h-full w-full flex flex-col">
            {/* Chat Header - 뒤로가기 버튼 추가 */}
            <div className="p-4 border-b border-[#e4e6eb] flex items-center">
                <button
                    onClick={onBack}
                    className="bg-transparent border-none cursor-pointer mr-3 text-[#1877f2] hover:text-[#166fe5]"
                >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    >
                        <path d="M19 12H5M12 19l-7-7 7-7" />
                    </svg>
                </button>

                <div className="flex-grow">
                    <div className="font-bold text-[16px]">{postTitle || `채팅방 ${chatRoomId}`}</div>
                </div>

                <button className="bg-transparent border-none cursor-pointer ml-2 text-[#1877f2] hover:text-[#166fe5]">
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    >
                        <circle cx="12" cy="12" r="10" />
                        <line x1="12" y1="16" x2="12" y2="12" />
                        <line x1="12" y1="8" x2="12.01" y2="8" />
                    </svg>
                </button>
            </div>

            {/* Chat Messages */}
            <div className="flex-grow overflow-y-auto p-4">
                {loading && <p className="text-center text-[#65676B]">채팅 내역을 불러오는 중...</p>}

                {error && <p className="text-center text-[#F02849]">오류가 발생했습니다: {error}</p>}

                {!loading && items.length === 0 && (
                    <p className="text-center text-[#65676B]">아직 메시지가 없습니다. 첫 메시지를 작성해보세요!</p>
                )}

                {items.length > 0 && (
                    <div>
                        {items.map((chat) => (
                            <div
                                key={chat.id}
                                className={`mb-3 flex flex-row ${chat.chatMemberId === currentMemberId ? "justify-end" : "justify-start"} items-end w-full`}
                            >
                                {chat.chatMemberId !== currentMemberId && (
                                    <div className="w-7 h-7 rounded-full bg-[#E4E6EB] mr-2 flex items-center justify-center text-xs font-bold">
                                        {chat.chatMemberName.charAt(0)}
                                    </div>
                                )}

                                <div className="flex flex-col max-w-[70%]">
                                    <div
                                        className={`${chat.chatMemberId === currentMemberId ? "bg-[#0084FF] text-white" : "bg-[#E4E6EB] text-black"
                                            } rounded-[18px] px-3 py-2 break-words inline-block max-w-full`}
                                    >
                                        {chat.content}
                                    </div>

                                    <div
                                        className={`text-[11px] text-[#65676B] mt-1 ${chat.chatMemberId === currentMemberId ? "self-end" : "self-start"
                                            }`}
                                    >
                                        {chat.chatMemberName} ·{" "}
                                        {chat.createdAt instanceof Date
                                            ? chat.createdAt.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
                                            : new Date(chat.createdAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                                    </div>
                                </div>
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>
                )}
            </div>

            {/* Chat Input */}
            <div className="p-4 border-t border-[#e4e6eb]">
                <form onSubmit={handleSendMessage} className="flex items-center">
                    <input
                        type="text"
                        value={newMessage}
                        onChange={handleMessageChange}
                        placeholder="메시지를 입력하세요..."
                        className="flex-grow py-2 px-3 border border-[#e4e6eb] rounded-[20px] outline-none text-[14px]"
                    />
                    <button
                        type="submit"
                        // disabled={!newMessage.trim() || !stompClientRef.current?.connected}
                        className={`ml-2 bg-transparent border-none ${newMessage.trim() && stompClientRef.current?.connected
                            ? "text-[#0084FF] cursor-pointer"
                            : "text-[#BCC0C4] cursor-not-allowed"
                            } p-2`}
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="20"
                            height="20"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                        >
                            <line x1="22" y1="2" x2="11" y2="13" />
                            <polygon points="22 2 15 22 11 13 2 9 22 2" />
                        </svg>
                    </button>
                </form>
            </div>
        </div>
    )
}

export default ChatRoom
