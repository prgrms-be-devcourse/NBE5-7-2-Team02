
import { Button, Navbar, NavbarBrand } from "flowbite-react";
import { useNavigate } from "react-router-dom";

export function NavBar() {
  const navigate = useNavigate();
  return (
      <div className="fixed top-0 left-0 right-0 w-full z-50 border-b-1 border-blue-900 dark:border-gray-600">
        <Navbar fluid rounded className="w-full !bg-bright dark:!bg-dark">
          <NavbarBrand href="https://flowbite-react.com">
            <img src="https://picsum.photos/200" className="mr-3 h-6 sm:h-9" alt="Flowbite React Logo" />
            <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white text-blue-900">programmers</span>
          </NavbarBrand>
          <div className="flex md:order-2">
            <Button
                className="!bg-blue-900 hover:!bg-blue-800"
                onClick={() => navigate("/login")}
            >로그인</Button>
          </div>
          <div className="flex md:order-2">
            <Button
                className="!bg-blue-900 hover:!bg-blue-800"
                onClick={() => navigate("/mypage")}
            >마이페이지</Button>
          </div>
        </Navbar>
      </div>
  );
}
