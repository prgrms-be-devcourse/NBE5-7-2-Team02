
import { Button, Navbar, NavbarBrand, Avatar, Dropdown, DropdownItem, DropdownDivider, DropdownHeader } from "flowbite-react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import darklogo from "../assets/images/programmers-logo-dark.png";
import lightlogo from "../assets/images/programmers-logo-light.png";

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