import { useEffect, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext"
import api from "../api/axiosInstance";

const OAuthCallback = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const hasProcessed = useRef(false);

  useEffect(() => {
    if (hasProcessed.current) return;
    hasProcessed.current = true;

    const params = new URLSearchParams(location.search);
    const accessToken = params.get("accessToken");
    const refreshToken = params.get("refreshToken");
    const error = params.get("error")

    if (accessToken && refreshToken) {
      login(accessToken, refreshToken);
      
      api.get("/member/me")
      .then(response => {
        const {name, job, course} = response.data;

        if(!name || !job || !course) {
            navigate("/signup");
        }else{
            navigate("/");
        }
      })
      .catch(error => {
        console.error("사용자 정보 가져오기 실패: ", error);
        navigate("/login");
      });
    } else if (error === "org") {
      alert("프로그래머스 교육 과정에 등록된 사용자만 가입할 수 있습니다.");
      navigate("/login");
    } else {
      navigate("/login");
    }
  }, [location, navigate, login]);

  return <div className="flex justify-center items-center h-screen">
    <div className="text-center">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-900 mx-auto"></div>
      <p className="mt-4 text-gray-600 dark:text-gray-300">로그인 처리 중...</p>
    </div>
  </div>;
};

export default OAuthCallback;
