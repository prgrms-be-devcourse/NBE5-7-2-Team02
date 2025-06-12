import { createContext, useState, useContext, useEffect } from "react";
import api from "../api/axiosInstance";
import { Member } from "@/types/Member";

interface AuthContextType {
  isAuthenticated: boolean;
  user: Member | null;
  login: (accessToken: string, refreshToken: string) => void;
  logout: () => void;
  setUser: React.Dispatch<React.SetStateAction<Member | null>>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<Member | null>(null);

  useEffect(() => {
    const accessToken = localStorage.getItem("accessToken");
    const savedUser = localStorage.getItem("user");

    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        console.error("로컬 유저 정보 파싱 실패", e);
      }
    }

    if (accessToken) {
      setIsAuthenticated(true);
      api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;

      api.get("/member/me")
        .then(response => {
          const raw = response.data;
          const user: Member = {
            id: raw.id,
            username: raw.name,
            profileImage: raw.profile_image,
            followerCount: raw.follower_count,
            followingCount: raw.following_count,
            following: raw.following,
            owner: raw.owner,
          };
          setUser(user);
          localStorage.setItem("user", JSON.stringify(user));
        })
        .catch(error => {
          console.error("사용자 정보 가져오기 오류:", error);
          logout();
        });
    }
  }, []);

  const login = (accessToken: string, refreshToken: string) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
    setIsAuthenticated(true);
  };

  const logout = () => {
    const refresh_token = localStorage.getItem("refreshToken");

    if (refresh_token) {
      api.post("/logout", { refresh_token })
        .catch(error => console.error("로그아웃 오류:", error));
    }

    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
    delete api.defaults.headers.common["Authorization"];
    setIsAuthenticated(false);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout, setUser }}>
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