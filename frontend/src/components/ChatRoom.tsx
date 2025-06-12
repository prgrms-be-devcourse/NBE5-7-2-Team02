"use client"

import type React from "react"
import { useState, useEffect, useRef } from "react"
import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs"
import SockJS from "sockjs-client"

function decodeJwtPayload(token: string): any | null {
    try {
        const base64Url = token.split('.')[1];
        if (!base64Url) return null;
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map(function (c) {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                })
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

interface ChatRoomProps {
    chatRoomId: string
    postTitle: string
    onBack: () => void // 뒤로가기 버튼 클릭 시 호출될 함수
}

interface ChatRes {
    id: number
    memberId: number
    memberName: string
    content: string
    createdAt: Date | string
}

interface ServerChatItem {
    id: number
    member_id: number
    member_name: string
    content: string
    created_at: string
}

// 백엔드 ChatMemberGetResponse DTO와 일치하도록 수정
interface ServerChatParticipant {
    chatroomId: number
    memberId: number
    memberName: string
    memberImage: string | null
    createdAt: string
    chatMemberStatus: string
}

// 프론트엔드에서 사용할 참여자 정보
interface ChatParticipant {
    id: number
    name: string
    image: string | null
    status: string
}

function ChatRoom({ chatRoomId, postTitle, onBack }: ChatRoomProps) {
    const [items, setItems] = useState<ChatRes[]>([])
    const [loading, setLoading] = useState<boolean>(true)
    const [error, setError] = useState<string | null>(null)
    const [newMessage, setNewMessage] = useState<string>("")

    const [participants, setParticipants] = useState<ChatParticipant[]>([])
    const [showParticipants, setShowParticipants] = useState<boolean>(false)
    const [loadingParticipants, setLoadingParticipants] = useState<boolean>(false)

    const stompClientRef = useRef<Client | null>(null)
    const subscriptionRef = useRef<StompSubscription | null>(null)
    const messagesEndRef = useRef<HTMLDivElement>(null)

    const [currentMemberId, setCurrentMemberId] = useState<number | null>(null);
    const participantsRef = useRef(participants);

    useEffect(() => {
        participantsRef.current = participants;
    }, [participants]);


    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (token) {
            const payload = decodeJwtPayload(token);
            if (payload && payload.memberId) {
                setCurrentMemberId(Number(payload.memberId));
            } else if (payload && payload.sub) {
                setCurrentMemberId(Number(payload.sub));
            }
        } else {
        }
    }, []);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
    }

    // STOMP 연결 해제 함수
    const disconnectStomp = () => {
        if (subscriptionRef.current) {
            subscriptionRef.current.unsubscribe();
            subscriptionRef.current = null;
        }
        if (stompClientRef.current?.active) {
            stompClientRef.current.deactivate();
            stompClientRef.current = null;
        } else {
        }
    };


    useEffect(() => {
        scrollToBottom()
    }, [items])

    useEffect(() => {
        setNewMessage("");
    }, [chatRoomId]);

    useEffect(() => {
        const fetchData = async () => {
            if (!chatRoomId) return;
            setLoading(true);
            setError(null);

            try {
                const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/chatroom/${chatRoomId}/message`, {
                    method: 'GET',
                });


                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) {
                        setError("인증에 실패했습니다. 다시 로그인해주세요.");
                        return;
                    }
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const resultContainer = await response.json();
                const resultFromServer: ServerChatItem[] = resultContainer;
                if (Array.isArray(resultFromServer)) {
                    const mappedResult: ChatRes[] = resultFromServer.map((item) => ({
                        id: item.id,
                        memberId: item.member_id,
                        memberName: item.member_name,
                        content: item.content,
                        createdAt: new Date(item.created_at),
                    }));
                    setItems(mappedResult);
                } else {
                    setItems([]);
                }
            } catch (e: any) {
                setError(e.message);
                setItems([]);
            } finally {
                setLoading(false);
            }
        }
        fetchData();
    }, [chatRoomId]);

    useEffect(() => {
        const fetchParticipants = async () => {
            if (!chatRoomId) return;
            setLoadingParticipants(true);

            try {
                const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/chatroom/${chatRoomId}/member`, {
                    method: 'GET',
                });

                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) {
                        return;
                    }
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const result = await response.json();
                const serverParticipants: ServerChatParticipant[] = result || [];
                const mappedParticipants: ChatParticipant[] = serverParticipants.map((p) => ({
                    id: p.member_id,
                    name: p.member_name || "이름",
                    image: p.member_image,
                    status: p.chat_member_status || "상태",
                }));
                setParticipants(mappedParticipants);
            } catch (err: any) {
                setParticipants([]);
            } finally {
                setLoadingParticipants(false);
            }
        }
        fetchParticipants();
    }, [chatRoomId]);

    useEffect(() => {
        if (!chatRoomId) {
            disconnectStomp();
            return;
        }

        const token = localStorage.getItem("accessToken");

        if (!token) {
        }

        const client = new Client({
            webSocketFactory: () => new SockJS(`${import.meta.env.VITE_BASE_URL}/ws/chatroom`),
            debug: () => {
            },
            reconnectDelay: 5000,
            connectHeaders: { Authorization: `Bearer ${token}` },
        });


        client.onConnect = () => {
            stompClientRef.current = client;
            const topic = `/sub/${chatRoomId}/message`;
            subscriptionRef.current = client.subscribe(topic, (message: IMessage) => {
                try {
                    const serverData: ServerChatItem = JSON.parse(message.body);
                    if (serverData && typeof serverData.id !== 'undefined') {
                        let memberName = serverData.member_name;
                        if (!memberName) {
                            // ref를 사용해서 최신 participants 목록을 참조
                            const participant = participantsRef.current.find(p => p.id === serverData.member_id);
                            memberName = participant ? participant.name : `User ${serverData.member_id}`;
                        }
                        if (!memberName && serverData.member_id === currentMemberId) {
                            // 현재 사용자의 경우, participants에 아직 없을 수 있으므로 기본값 설정 가능
                            const currentUser = participants.find(p => p.id === currentMemberId);
                            memberName = currentUser ? currentUser.name : "나";
                        }


                        const mappedData: ChatRes = {
                            id: serverData.id,
                            memberId: serverData.member_id,
                            memberName: memberName,
                            content: serverData.content,
                            createdAt: new Date(serverData.created_at),
                        };

                        setItems((prevItems) => {
                            const currentItems = Array.isArray(prevItems) ? prevItems : [];
                            const exists = currentItems.some((item) => item.id === mappedData.id);
                            if (exists) {
                                return currentItems;
                            }
                            return [...currentItems, mappedData];
                        });
                    } else {
                    }
                } catch (errorInCallback) {
                }
            });
        };
        client.onStompError = (frame) => {
            setError(`STOMP Error: ${frame.headers["message"] || "Connection failed"}`);
        };
        client.onWebSocketError = () => {
            setError("WebSocket 연결에 실패했습니다. 네트워크 상태를 확인해주세요.");
        };
        client.onDisconnect = () => {
        };

        client.activate();

        return () => {
            disconnectStomp();
        };
    }, [chatRoomId]);

    const handleMessageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewMessage(event.target.value);
    };

    const handleSendMessage = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!newMessage.trim() || !stompClientRef.current?.connected || currentMemberId === null) {
            return;
        }

        const messageToSend = { memberId: currentMemberId, content: newMessage };
        const destination = `/pub/${chatRoomId}/message`;
        try {
            stompClientRef.current.publish({ destination: destination, body: JSON.stringify(messageToSend) });
            setNewMessage("");
        } catch (error) {
        }
    };

    const toggleParticipantsList = () => {
        setShowParticipants((prev) => !prev);
    };

    const handleBackButtonPress = () => {
        disconnectStomp();
        onBack();
    };

    const handleLeaveChatRoom = async () => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            alert("로그인이 필요합니다.");
            return;
        }

        try {
            const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/chatroom/${chatRoomId}/member`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify({ chatMemberStatus: 'LEFT' }),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ message: '서버 응답 오류' }));
                throw new Error(errorData.message || `채팅방 나가기 실패: ${response.status}`);
            }

            disconnectStomp();
            onBack();

        } catch (error) {
            console.error("Failed to leave chat room:", error);
            alert(`채팅방을 나가는 중 오류가 발생했습니다: ${error instanceof Error ? error.message : '알 수 없는 오류'}`);
        }
    };

    return (
        <div className="h-full w-full flex flex-col relative">
            {/* Chat Header */}
            <div className="p-4 border-b border-[#e4e6eb] flex items-center">
                <button
                    onClick={handleBackButtonPress}
                    className="bg-transparent border-none cursor-pointer mr-3 text-[#1877f2] hover:text-[#166fe5]"
                    aria-label="Back to chat list"
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
                <button
                    onClick={toggleParticipantsList}
                    className="bg-transparent border-none cursor-pointer ml-2 text-[#1877f2] hover:text-[#166fe5]"
                    aria-label="Show chat participants"
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
                        <circle cx="12" cy="12" r="10" /> <line x1="12" y1="16" x2="12" y2="12" />
                        <line x1="12" y1="8" x2="12.01" y2="8" />
                    </svg>
                </button>
            </div>

            {/* Main Chat Area */}
            <div className="flex-grow overflow-hidden relative">
                {/* Chat Messages */}
                <div className="flex-grow overflow-y-auto p-4 h-full">
                    {loading && <p className="text-center text-[#65676B]">채팅 내역을 불러오는 중...</p>}
                    {error && <p className="text-center text-[#F02849]">오류가 발생했습니다: {error}</p>}
                    {!loading && items.length === 0 && (
                        <p className="text-center text-[#65676B]">아직 메시지가 없습니다. 첫 메시지를 작성해보세요!</p>
                    )}
                    {items.length > 0 && (
                        <div>
                            {items.map((chat) => {
                                const isMyMessage = currentMemberId !== null && chat.memberId === currentMemberId;
                                return (
                                    <div
                                        key={chat.id}
                                        className={`mb-3 flex flex-row ${isMyMessage ? "justify-end" : "justify-start"} items-end w-full`}
                                    >
                                        {!isMyMessage && (
                                            <div className="w-7 h-7 rounded-full bg-[#E4E6EB] mr-2 flex items-center justify-center text-xs font-bold">
                                                {chat.memberName && typeof chat.memberName === "string" && chat.memberName.length > 0
                                                    ? chat.memberName.charAt(0).toUpperCase()
                                                    : "?"}
                                            </div>
                                        )}
                                        <div className="flex flex-col max-w-[70%]">
                                            <div
                                                className={`${isMyMessage ? "bg-[#0084FF] text-white" : "bg-[#E4E6EB] text-black"} rounded-[18px] px-3 py-2 break-words inline-block max-w-full`}
                                            >
                                                {chat.content}
                                            </div>
                                            <div
                                                className={`text-[11px] text-[#65676B] mt-1 ${isMyMessage ? "self-end" : "self-start"}`}
                                            >
                                                {chat.memberName || "알 수 없는 사용자"} ·{" "}
                                                {chat.createdAt instanceof Date
                                                    ? chat.createdAt.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
                                                    : new Date(chat.createdAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                            <div ref={messagesEndRef} />
                        </div>
                    )}
                </div>

                {/* Participant List Panel - Overlay */}
                {showParticipants && (
                    <div className="absolute top-0 right-0 w-64 md:w-72 lg:w-80 h-full bg-white/95 backdrop-blur-sm flex flex-col overflow-y-auto transition-all duration-300 ease-in-out shadow-lg z-10 border-l border-[#e4e6eb]">
                        <div className="p-4 border-b border-[#e4e6eb] flex justify-between items-center bg-gradient-to-r from-[#f0f2f5] to-white">
                            <h3 className="font-bold text-md text-[#1877f2]">참여중인 멤버</h3>
                            <button
                                onClick={() => {
                                    if (window.confirm("채팅방을 나가시겠습니까?")) {
                                        handleLeaveChatRoom();
                                    }
                                }}
                                className="bg-transparent border-none cursor-pointer text-black hover:text-gray-600"
                                aria-label="Leave chat room"
                            >
                                <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    width="18"
                                    height="18"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    strokeWidth="2"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                >
                                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                                    <polyline points="16,17 21,12 16,7" />
                                    <line x1="21" y1="12" x2="9" y2="12" />
                                </svg>
                            </button>

                        </div>

                        <div className="flex-grow p-2">
                            {loadingParticipants && (
                                <div className="flex justify-center items-center h-20">
                                    <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-[#1877f2]"></div>
                                </div>
                            )}
                            {!loadingParticipants && participants.length === 0 && (
                                <div className="text-center text-sm text-[#65676B] p-6 bg-[#f7f9fb] rounded-lg m-2">
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        width="24"
                                        height="24"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        strokeWidth="2"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        className="mx-auto mb-2 text-[#65676B]"
                                    >
                                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path> <circle cx="9" cy="7" r="4"></circle>
                                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path> <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                    </svg>
                                    참여중인 멤버가 없습니다.
                                </div>
                            )}
                            {!loadingParticipants && (
                                <div className="space-y-1">
                                    {participants.map((participant) => (
                                        <div
                                            key={participant.id}
                                            className="flex items-center justify-between p-2 hover:bg-[#f0f2f5] rounded-md transition-colors"
                                        >
                                            <button
                                                onClick={() => window.location.href = `/mypage/${participant.id}`}
                                                className="flex items-center w-full text-left hover:bg-[#f0f2f5] rounded-md p-1 transition-colors cursor-pointer"
                                            >
                                                {participant.image ? (
                                                    <img
                                                        src={participant.image || "/placeholder.svg"}
                                                        alt={participant.name || "참가자"}
                                                        className="w-9 h-9 rounded-full object-cover mr-3 shadow-sm"
                                                    />
                                                ) : (
                                                    <div className="w-9 h-9 rounded-full bg-gradient-to-br from-[#0084FF] to-[#1877f2] text-white flex items-center justify-center text-sm font-semibold mr-3 shadow-sm">
                                                        {participant.name && typeof participant.name === "string" && participant.name.length > 0
                                                            ? participant.name.charAt(0).toUpperCase()
                                                            : "?"}
                                                    </div>
                                                )}
                                                <div>
                                                    <span className="text-sm font-medium">{participant.name || "이름 없음"}</span>
                                                    {participant.id === currentMemberId && currentMemberId !== null && (
                                                        <span className="ml-2 text-xs bg-[#e7f3ff] text-[#1877f2] px-1.5 py-0.5 rounded-full">
                                                            나
                                                        </span>
                                                    )}
                                                    <div className="text-xs text-[#65676B] capitalize">
                                                        {participant.status && typeof participant.status === "string"
                                                            ? participant.status.toLowerCase()
                                                            : "상태 없음"}
                                                    </div>
                                                </div>
                                            </button>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
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
                        className="flex-grow py-2 px-3 border border-[#e4e6eb] rounded-[20px] outline-none text-[14px] text-dark"
                    />
                    <button
                        type="submit"
                        disabled={!newMessage.trim() || !stompClientRef.current?.connected}
                        className={`ml-2 bg-transparent border-none ${newMessage.trim() && stompClientRef.current?.connected ? "text-[#0084FF] cursor-pointer" : "text-[#BCC0C4] cursor-not-allowed"} p-2`}
                        aria-label="Send message"
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
                            <line x1="22" y1="2" x2="11" y2="13" /> <polygon points="22 2 15 22 11 13 2 9 22 2" />
                        </svg>
                    </button>
                </form>
            </div>
        </div>
    );
}

export default ChatRoom;