
import { Button, Navbar, NavbarBrand, Avatar, Dropdown, DropdownItem, DropdownDivider, DropdownHeader } from "flowbite-react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import darklogo from "../assets/images/programmers-logo-dark.png";
import lightlogo from "../assets/images/programmers-logo-light.png";
import { Link } from "react-router-dom"

export function NavBar() {
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();

  const handleLogin = () => {
    navigate("/login");
  };

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const handleLogoClick = (e: React.MouseEvent<HTMLImageElement>) => {
    e.preventDefault(); // 기본 a 태그 새로고침 방지
    navigate("/");      // React Router 방식으로 이동
  };


  return (
      <div className="fixed top-0 left-0 right-0 w-full z-50 border-b-1 border-blue-900 dark:border-gray-600">
        <Navbar fluid rounded className="w-full !bg-bright dark:!bg-dark">
          <NavbarBrand href="/">
            <picture>
              <source srcSet={lightlogo} media="(prefers-color-scheme: dark)" />
              <img
                  src={darklogo}
                  className="mr-3 h-6 sm:h-9"
                  alt="Flowbite React Logo"
                  onClick={handleLogoClick} // ✅ 이 부분
              />
            </picture>

          </NavbarBrand>
          <div className="flex md:order-2">
          <Link
            to="/chats"
            className="inline-flex items-center justify-center mr-3 p-2 text-gray-700 dark:text-gray-200 bg-gray-100 dark:bg-gray-700 rounded-full hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
            title="참여중인 채팅방 목록 보기"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
              />
            </svg>
            <span className="sr-only">참여중인 채팅방 목록 보기</span>
          </Link>
            {isAuthenticated ? (
                <Dropdown
                    arrowIcon={false}
                    inline
                    label={
                      <Avatar
                          alt="User avatar"
                          img={user?.profileImage}
                          rounded
                          className="cursor-pointer"
                      />
                    }
                >
                  <DropdownHeader>
                    <span className="block text-sm">{user?.username || '사용자'}</span>
                  </DropdownHeader>
                  <DropdownItem onClick={() => navigate("/mypage")}>
                    프로필
                  </DropdownItem>
                  <DropdownDivider />
                  <DropdownItem onClick={handleLogout}>
                    로그아웃
                  </DropdownItem>
                </Dropdown>
            ) : (
                <Button
                    className="!bg-blue-900 hover:!bg-blue-800"
                    onClick={handleLogin}
                >
                  로그인
                </Button>
            )}
          </div>
        </Navbar>
      </div>
  );
}