
import { Button, Navbar, NavbarBrand, Avatar, Dropdown, DropdownItem, DropdownDivider, DropdownHeader } from "flowbite-react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

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

  return (
      <div className="fixed top-0 left-0 right-0 w-full z-50 border-b-1 border-blue-900 dark:border-gray-600">
        <Navbar fluid rounded className="w-full !bg-bright dark:!bg-dark">
          <NavbarBrand href="/">
            <img
                src="https://picsum.photos/200"
                className="mr-3 h-6 sm:h-9"
                alt="Flowbite React Logo"
            />
            <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white text-blue-900">
            programmers
          </span>
          </NavbarBrand>
          <div className="flex md:order-2">
            {isAuthenticated ? (
                <Dropdown
                    arrowIcon={false}
                    inline
                    label={
                      <Avatar
                          alt="User avatar"
                          img="https://picsum.photos/200"
                          rounded
                          className="cursor-pointer"
                      />
                    }
                >
                  <DropdownHeader>
                    <span className="block text-sm">{user?.name || '사용자'}id:{user?.member_id}</span>
                  </DropdownHeader>
                  <DropdownItem onClick={() => navigate(`/mypage?memberId=${user?.member_id}`)}>
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