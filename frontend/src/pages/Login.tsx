import { useState } from "react";
import { Button, Card } from "flowbite-react";
import { Github } from "lucide-react";

const Login = () => {
  const [isLoading, setIsLoading] = useState(false);

  const handleGithubLogin = async () => {
    setIsLoading(true);
    try {
      const githubAuthUrl = `${import.meta.env.VITE_BASE_URL}/oauth2/authorization/github`;
      window.location.href = githubAuthUrl;

    } catch (error) {
      console.error("로그인 과정에서 오류 발생:", error);
      setIsLoading(false);
    }
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-bright dark:bg-dark p-4">
        <Card className="w-full max-w-md dark:border-gray-600 dark:!bg-dark">
          <div className="text-center mb-6">
            <h2 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
              로그인
            </h2>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">
              소셜 계정으로 간편하게 로그인하세요
            </p>
          </div>

          <div className="flex flex-col gap-4">
            <Button
                color="dark"
                className="flex items-center justify-center gap-2 w-full"
                onClick={handleGithubLogin}
                disabled={isLoading}
            >
              <Github className="h-5 w-5" />
              GitHub로 로그인
            </Button>
          </div>

          <div className="text-center mt-6">
            <p className="text-sm text-gray-500 dark:text-gray-400">
              로그인함으로써 서비스 이용약관 및 개인정보 처리방침에 동의합니다.
            </p>
          </div>
        </Card>
      </div>
  );
};

export default Login;
