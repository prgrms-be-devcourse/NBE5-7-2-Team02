import axios from "axios";

// 환경 변수로부터 API URL 가져오기
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

// 예외처리
api.interceptors.response.use(
  (res) => res,
  (err) => {
    const res = err.response;
    if (res?.data?.code && res?.data?.message) {
      console.error(`[${res.data.code}] ${res.data.message}`);
      alert(res.data.message); // 💬 또는 toast, modal 등으로 교체 가능
    } else {
      alert("예기치 못한 오류가 발생했습니다.");
    }
    return Promise.reject(err); // 필수: 호출자에서 catch 가능하게 함
  }
);

export default api;