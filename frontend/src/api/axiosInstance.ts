import axios from "axios";

// 환경 변수로부터 API URL 가져오기
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

export default api;