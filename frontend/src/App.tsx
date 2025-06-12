import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Signup from "./pages/Signup";
import OAuthCallback from "./components/OAuthCallback";
import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import { NavBar } from "./components/NavBar";
import Home from "./pages/Home";
import MyPage from "./pages/MyPage";
import Login from "./pages/Login";
import EditPostPage from "./pages/EditPostPage";
import { AuthProvider } from "./context/AuthContext";
import ChatRoomList from "./pages/ChatRoomList";
// import ChatRoom from "./components/ChatRoom";

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="min-h-screen bg-bright dark:bg-dark">
          <div className="pt-16">
            <NavBar />
          </div>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/mypage" element={<MyPage />} /> // 내 마이페이지
            <Route path="/mypage/:postMemberId" element={<MyPage />} /> // 타인 마이페이지
            <Route path="/login" element={<Login />} />
            <Route path="/posts/edit/:postId" element={<EditPostPage />} />
            {/* 잘못된 경로로 접근시 Home으로 이동 */}
            <Route path="*" element={<Navigate to="/" replace />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/callback" element={<OAuthCallback />} />
            <Route path="/chats" element={<ChatRoomList />} />
            {/*<Route path="/ChatRoom/:chatRoomId" element={<ChatRoom />} />*/}
          </Routes>
          <ToastContainer
            position="top-center"
            autoClose={2000}
            hideProgressBar={false}
            closeOnClick
            pauseOnHover
          />
        </div>
      </AuthProvider>
    </Router>
  );
}
export default App;