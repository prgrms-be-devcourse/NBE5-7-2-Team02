
import { createContext, useState, useContext, useEffect } from "react";
import api from "../api/axiosInstance";

interface User {
  member_id: number;
  name: string;
  job?: string;
  course?: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  login: (accessToken: string, refreshToken: string) => void;
  logout: () => void;
  refreshToken: () => Promise<string | null>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<User | null>(null);

  // 토큰이 있으면 사용자 인증 상태로 설정
  useEffect(() => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      setIsAuthenticated(true);
      api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
      
      // 사용자 정보 가져오기 (API 엔드포인트에 맞게 수정 필요)
      api.get("/member/me")
        .then(response => {
          setUser(response.data.data);
        })
        .catch(error => {
          console.error("사용자 정보 가져오기 오류:", error);
          logout();
        });
    }
  }, []);

  // 액세스 토큰 갱신 함수
  const refreshToken = async (): Promise<string | null> => {
    try {
      const refreshToken = localStorage.getItem("refreshToken");
      const response = await api.post("/token/refresh", { refreshToken });
      
      if (response.data?.accessToken) {
        const newAccessToken = response.data.accessToken;
        localStorage.setItem("accessToken", newAccessToken);
        api.defaults.headers.common["Authorization"] = `Bearer ${newAccessToken}`;
        return newAccessToken;
      }
    } catch (error) {
      console.error("토큰 갱신 오류:", error);
      logout();
    }
    return null;
  };

  const login = (accessToken: string, refreshToken: string) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    setIsAuthenticated(true);
  };

  const logout = () => {
    const refreshToken = localStorage.getItem("refreshToken");
    
    if (refreshToken) {
      // 서버에 로그아웃 요청
      api.post("/logout", { refreshToken })
        .catch(error => console.error("로그아웃 오류:", error));
    }
    
    // 로컬 스토리지 및 상태 초기화
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    delete api.defaults.headers.common["Authorization"];
    setIsAuthenticated(false);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout, refreshToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};