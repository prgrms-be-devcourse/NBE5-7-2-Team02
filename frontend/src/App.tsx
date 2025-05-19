import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import {NavBar} from "./components/NavBar";
import Home from "./pages/Home";
import MyPage from "./pages/MyPage";
import Login from "./pages/Login";
function App() {
  return (
      <Router>
        <div className="min-h-screen bg-bright dark:bg-dark">
          <div className="pt-16">
            <NavBar />
          </div>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/mypage" element={<MyPage />} /> {/*자신의 마이페이지*/}
            <Route path="/mypage/:userId" element={<MyPage />} /> {/*특정 유저의 마이페이지*/}
            <Route path="/login" element={<Login />} />
            {/* 잘못된 경로로 접근시 Home으로 이동 */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </Router>
  );
}

export default App;