
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Card, Label, Select, TextInput } from "flowbite-react";
import { StatusCodes } from "http-status-codes";
import api from "../api/axiosInstance";

const Signup = () => {
  const [name, setName] = useState("");
  const [job, setJob] = useState("");
  const [course, setCourse] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const navigate = useNavigate();

  const jobOptions = ["프론트엔드 개발자", "백엔드 개발자", "풀스택 개발자", "기획자", "디자이너", "PM", "기타"];
  const courseOptions = ["데브코스", "데이터 사이언스 코스", "디자인 코스", "기타"];


  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!name.trim()) {
      setError("이름을 입력해주세요.");
      return;
    }
    
    if (!job) {
      setError("직무를 선택해주세요.");
      return;
    }
    
    if (!course) {
      setError("코스를 선택해주세요.");
      return;
    }
    
    try {
      setIsLoading(true);
      setError(null);
      
      const response = await api.post("/signup", {
        name,
        job,
        course
      });
      
      // 회원가입 성공 시, 홈페이지로 리다이렉트
      if (response.status === StatusCodes.CREATED) {
        navigate("/");
      }
    } catch (err) {
      console.error("회원가입 과정에서 오류 발생:", err);
      setError("회원가입 중 오류가 발생했습니다. 다시 시도해 주세요.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-bright dark:bg-dark p-4">
      <Card className="w-full max-w-md dark:border-gray-600 dark:!bg-dark">
        <div className="text-center mb-6">
          <h2 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            회원가입
          </h2>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">
            추가 정보를 입력해주세요
          </p>
        </div>
        
        {error && (
          <div className="p-4 mb-4 text-sm text-red-700 bg-red-100 rounded-lg dark:bg-red-200 dark:text-red-800" role="alert">
            {error}
          </div>
        )}
        
        <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
          <div>
            <div className="mb-2 block">
              <Label htmlFor="name">이름</Label>
            </div>
            <TextInput
              id="name"
              placeholder="이름을 입력하세요"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>
          
          <div>
            <div className="mb-2 block">
              <Label htmlFor="job">직무</Label>
            </div>
            <Select
              id="job"
              value={job}
              onChange={(e) => setJob(e.target.value)}
              required
            >
              <option value="" disabled>직무를 선택하세요</option>
              {jobOptions.map((option) => (
                <option key={option} value={option}>{option}</option>
              ))}
            </Select>
          </div>
          
          <div>
            <div className="mb-2 block">
              <Label htmlFor="course">코스</Label>
            </div>
            <Select
              id="course"
              value={course}
              onChange={(e) => setCourse(e.target.value)}
              required
            >
              <option value="" disabled>코스를 선택하세요</option>
              {courseOptions.map((option) => (
                <option key={option} value={option}>{option}</option>
              ))}
            </Select>
          </div>
          
          <Button 
            type="submit" 
            className="!bg-blue-900 hover:!bg-blue-800 mt-4"
            disabled={isLoading}
          >
            {isLoading ? '처리 중...' : '가입 완료'}
          </Button>
        </form>
      </Card>
    </div>
  );
};

export default Signup;
