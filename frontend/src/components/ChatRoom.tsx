"use client"

import type React from "react"
import { useState, useEffect, useRef } from "react"
import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs"
import SockJS from "sockjs-client"

// Helper function to decode JWT
// WARNING: This is a very basic decoder and does not verify the token signature.
// For production, use a library like jwt-decode and ensure server-side verification.
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
        // console.error("Failed to decode JWT payload:", e);
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
    member_name: string // 서버 응답에 이 필드가 포함되어야 가장 좋습니다.
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

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (token) {
            const payload = decodeJwtPayload(token);
            // Assuming the memberId is stored in a claim named 'memberId' or 'sub' (standard for subject)
            // Adjust 'memberId' or 'sub' based on your actual JWT payload structure
            if (payload && payload.memberId) {
                setCurrentMemberId(Number(payload.memberId));
            } else if (payload && payload.sub) { // Fallback to 'sub' if 'memberId' is not present
                setCurrentMemberId(Number(payload.sub));
                // console.log("[Auth] Current member ID set from JWT 'sub' claim:", payload.sub);
            } else {
                // console.warn("[Auth] Could not find memberId or sub in JWT payload.", payload);
            }
        } else {
            // console.warn("[Auth] No accessToken found in localStorage for setting currentMemberId.");
        }
    }, []); // Run once on component mount

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
    }

    // STOMP 연결 해제 함수
    const disconnectStomp = () => {
        // console.log("[STOMP Disconnect Function] Attempting to disconnect STOMP client.");
        if (subscriptionRef.current) {
            subscriptionRef.current.unsubscribe();
            subscriptionRef.current = null;
            // console.log("[STOMP Disconnect Function] Unsubscribed from STOMP topic.");
        }
        if (stompClientRef.current?.active) {
            stompClientRef.current.deactivate();
            stompClientRef.current = null;
            // console.log("[STOMP Disconnect Function] Deactivated STOMP client.");
        } else {
            // console.log("[STOMP Disconnect Function] STOMP client not active or already null.");
        }
    };


    useEffect(() => {
        scrollToBottom()
    }, [items])

    useEffect(() => {
        const fetchData = async () => {
            if (!chatRoomId) return;
            setLoading(true);
            setError(null);

            try {
                const response = await fetch(`http://localhost:8080/api/chatroom/${chatRoomId}/message`, {
                    method: 'GET',
                });


                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) {
                        // console.error("Authentication error:", response.status, await response.text());
                        setError("인증에 실패했습니다. 다시 로그인해주세요.");
                        return;
                    }
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const resultContainer = await response.json();
                const resultFromServer: ServerChatItem[] = resultContainer.data;
                if (Array.isArray(resultFromServer)) {
                    const mappedResult: ChatRes[] = resultFromServer.map((item) => ({
                        id: item.id,
                        memberId: item.member_id,
                        memberName: item.member_name, // 초기 로딩 시에는 서버가 이름을 줄 것으로 기대
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
                const response = await fetch(`http://localhost:8080/api/chatroom/${chatRoomId}/member`, {
                    method: 'GET',
                });

                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) {
                        // console.error("Authentication error fetching participants:", response.status, await response.text());
                        return;
                    }
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const result = await response.json();
                const serverParticipants: ServerChatParticipant[] = result.data || [];
                const mappedParticipants: ChatParticipant[] = serverParticipants.map((p) => ({
                    id: p.memberId,
                    name: p.memberName || "Unknown User",
                    image: p.memberImage,
                    status: p.chatMemberStatus || "UNKNOWN",
                }));
                setParticipants(mappedParticipants);
            } catch (err: any) {
                // console.error("Failed to fetch participants:", err.message);
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
            // console.warn("[STOMP Setup] No accessToken found in localStorage. STOMP connection might fail or be unauthorized.");
        }

        const client = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/ws/chatroom"),
            // debug: (str) => {
            //     console.log("STOMP DEBUG: " + str)
            // },
            reconnectDelay: 5000,
            connectHeaders: { Authorization: `Bearer ${token}` },
        });


        client.onConnect = (frame) => {
            // console.log("[STOMP] Connected to server:", frame);
            stompClientRef.current = client;
            const topic = `/sub/${chatRoomId}/message`;
            subscriptionRef.current = client.subscribe(topic, (message: IMessage) => {
                // console.log("[STOMP MSG RECEIVED] Raw body (string):", message.body);
                try {
                    const serverData: ServerChatItem = JSON.parse(message.body); // 직접 파싱
                    // console.log("[STOMP MSG RECEIVED] Parsed serverData (object):", serverData);

                    // serverData가 유효한지 확인 (id 필드 존재 여부 등)
                    if (serverData && typeof serverData.id !== 'undefined') {
                        // chat_member_name이 STOMP 메시지에 없다면, participants 목록에서 찾거나 임시 이름 사용
                        let memberName = serverData.member_name; // 서버가 이름을 준다면 사용
                        if (!memberName) {
                            const participant = participants.find(p => p.id === serverData.member_id);
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
                            memberName: memberName, // 수정된 이름 사용
                            content: serverData.content,
                            createdAt: new Date(serverData.created_at),
                        };
                        // console.log("[STOMP] Mapped data for state update:", mappedData);

                        setItems((prevItems) => {
                            const currentItems = Array.isArray(prevItems) ? prevItems : [];
                            const exists = currentItems.some((item) => item.id === mappedData.id);
                            if (exists) {
                                // console.warn(`[STOMP setItems] Duplicate message ID ${mappedData.id} (content: "${mappedData.content}") detected. Not adding.`);
                                return currentItems;
                            }
                            // console.log(`[STOMP setItems] Adding new message ID ${mappedData.id} (content: "${mappedData.content}")`);
                            return [...currentItems, mappedData];
                        });
                    } else {
                        // console.warn("[STOMP] Parsed message does not appear to be a valid ServerChatItem. Parsed data:", serverData);
                    }
                } catch (errorInCallback) {
                    // console.error("[STOMP] Error parsing or processing message:", errorInCallback, "Raw message body for error:", message.body);
                }
            });
        };
        client.onStompError = (frame) => {
            // console.error("[STOMP] Broker error:", frame.headers["message"], frame.body);
            setError(`STOMP Error: ${frame.headers["message"] || "Connection failed"}`);
        };
        client.onWebSocketError = (event) => {
            // console.error("[STOMP] WebSocket error event:", event);
            setError("WebSocket 연결에 실패했습니다. 네트워크 상태를 확인해주세요.");
        };
        client.onDisconnect = () => {
            // console.log("[STOMP] Disconnected (from onDisconnect callback)");
        };

        // console.log("[STOMP Setup] Activating STOMP client...");
        client.activate();

        return () => {
            // console.log(`[STOMP Cleanup Effect] Cleaning up STOMP for chatRoomId: ${chatRoomId}`);
            disconnectStomp();
        };
    }, [chatRoomId, participants]); // participants를 의존성 배열에 추가 (memberName 찾기 위해)

    const handleMessageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNewMessage(event.target.value);
    };

    const handleSendMessage = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!newMessage.trim() || !stompClientRef.current?.connected || currentMemberId === null) {
            // console.warn("[SendMessage] Cannot send: message empty, STOMP not connected, or currentMemberId is null.");
            return;
        }

        // 메시지 전송 시에는 chatMemberName을 보내지 않습니다.
        // 서버가 memberId를 기반으로 조회하여 응답 메시지에 포함시켜야 합니다.
        const messageToSend = { memberId: currentMemberId, content: newMessage };
        const destination = `/pub/${chatRoomId}/message`;
        try {
            stompClientRef.current.publish({ destination: destination, body: JSON.stringify(messageToSend) });
            setNewMessage("");
        } catch (error) {
            // console.error("[SendMessage] Failed to send message:", error);
        }
    };

    const toggleParticipantsList = () => {
        setShowParticipants((prev) => !prev);
    };

    const handleChangeParticipantState = (participantId: number) => {
        // console.log(`Change state for participant ID: ${participantId}`);
    };

    const handleSelfStatusChange = async (newStatus: string) => {
        // console.log(`My (ID: ${currentMemberId}) status changed to: ${newStatus}`);
    };

    const currentUserParticipant = participants.find((p) => p.id === currentMemberId);

    const handleBackButtonPress = () => {
        // console.log("[handleBackButtonPress] Back button pressed. Disconnecting STOMP and calling onBack.");
        disconnectStomp();
        onBack();
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
                                onClick={toggleParticipantsList}
                                className="text-[#65676B] hover:text-[#1877f2] transition-colors"
                                aria-label="Close participants list"
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
                                    <line x1="18" y1="6" x2="6" y2="18"></line> <line x1="6" y1="6" x2="18" y2="18"></line>
                                </svg>
                            </button>
                        </div>

                        {currentUserParticipant && (
                            <div className="p-4 border-b border-[#e4e6eb] bg-[#f7f9fb]">
                                <div className="text-xs font-bold uppercase text-[#65676B] mb-2">내 상태</div>
                                <select
                                    className="w-full p-2 bg-white border border-[#e4e6eb] rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#1877f2] focus:border-transparent"
                                    value={currentUserParticipant.status || "ONLINE"}
                                    onChange={(e) => handleSelfStatusChange(e.target.value)}
                                >
                                    <option value="ONLINE">ONLINE</option>
                                    <option value="OFFLINE">OFFLINE</option>
                                    <option value="AWAY">AWAY (자리비움)</option>
                                    <option value="DO_NOT_DISTURB">DO_NOT_DISTURB (방해금지)</option>
                                    <option value="LEFT">LEFT (나감)</option>
                                </select>
                            </div>
                        )}

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
                                            <div className="flex items-center">
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
                                            </div>
                                            {participant.id !== currentMemberId && (
                                                <button
                                                    onClick={() => handleChangeParticipantState(participant.id)}
                                                    className="text-xs bg-white hover:bg-[#f0f2f5] text-[#1877f2] border border-[#e4e6eb] py-1 px-2 rounded-md transition-colors"
                                                    aria-label={`${participant.name || "참가자"}님 상태 변경`}
                                                >
                                                    <svg
                                                        xmlns="http://www.w3.org/2000/svg"
                                                        width="12"
                                                        height="12"
                                                        viewBox="0 0 24 24"
                                                        fill="none"
                                                        stroke="currentColor"
                                                        strokeWidth="2"
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                        className="inline"
                                                    >
                                                        <circle cx="12" cy="12" r="1"></circle> <circle cx="19" cy="12" r="1"></circle>
                                                        <circle cx="5" cy="12" r="1"></circle>
                                                    </svg>
                                                </button>
                                            )}
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
                        className="flex-grow py-2 px-3 border border-[#e4e6eb] rounded-[20px] outline-none text-[14px]"
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