import axios, { AxiosError } from "axios";

// API 기본 설정
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 토큰 관련 에러 처리
let isRefreshing = false;
let failedQueue: { resolve: (value: unknown) => void; reject: (reason?: any) => void }[] = [];

const processQueue = (error: AxiosError | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });

  failedQueue = [];
};

api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config as any;

    // 401 에러 && 토큰 만료 && 토큰 재발급 요청이 아닌 경우
    if (
      error.response.data.code === "JWT-002" &&
      !originalRequest._retry &&
      originalRequest.url !== "/token/refresh"
    ) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refresh_token = localStorage.getItem("refreshToken");

        if (!refresh_token) {
          // 리프레시 토큰이 없으면 로그아웃
          handleLogout();
          return Promise.reject(error);
        }

        const response = await api.post("/token/refresh", {
          refresh_token
        });

         if (response.data.data?.access_token) {

          const newAccessToken = response.data.data.access_token;
          const newRefreshToken = response.data.data.refresh_token;
          localStorage.setItem("accessToken", newAccessToken);
          localStorage.setItem("refreshToken", newRefreshToken);
          api.defaults.headers.common["Authorization"] = `Bearer ${newAccessToken}`;

          // 대기 중인 요청 처리
          processQueue(null, newAccessToken);

          // 원래 요청 재시도
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return api(originalRequest);
        } else {
          // 토큰 갱신 실패 시 로그아웃
          handleLogout();
          return Promise.reject(error);
        }
      } catch (refreshError) {
        // 리프레시 토큰도 만료된 경우 로그아웃
        processQueue(refreshError as AxiosError);
        handleLogout();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // 리프레시 토큰도 만료된 경우 (403 또는 특정 에러 코드)
    if (
      error.response?.data.code === "JWT-007" ||
      (error.response?.data?.message &&
       error.response?.data?.message.includes("refresh token expired"))
    ) {
      handleLogout();
    }

    const res = error.response;
    if (res?.data?.code && res?.data?.message) {
      console.error(`[${res.data.code}] ${res.data.message}`);
      if (res.data.code !== "CHAT-MEMBER-001") {
        alert(res.data.message); // 💬 또는 toast, modal 등으로 교체 가능
      }
    } else {
      alert("예기치 못한 오류가 발생했습니다.");
    }

    return Promise.reject(error);
  }
);

// 로그아웃 처리 함수
const handleLogout = () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  delete api.defaults.headers.common["Authorization"];


  window.location.href = "/";
};

export default api;