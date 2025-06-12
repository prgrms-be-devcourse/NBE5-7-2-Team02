
import {
  Avatar,
  Button,
  Card,
  TextInput
} from "flowbite-react";
import {useState} from "react";
import {ModalBoardForm} from "./ModalBoardForm";
import { useAuth } from "../context/AuthContext";

export function PreBoardForm() {
  const [openModal, setOpenModal] = useState(false);
  const { user } = useAuth();
  const closeModalHandler = () => setOpenModal(false);

  return (
      <Card className="flex flex-col gap-4 w-full bg-bright dark:!bg-dark">
        <div className="flow-root">
          <ul className="divide-y divide-gray-200 dark:divide-gray-700">
            <li className="py-3 sm:py-4">
              <div className="flex items-center space-x-4">
                <div className="shrink-0">
                  <Avatar img={user?.profileImage} rounded/>
                </div>
                <div className="min-w-0 flex-1">
                  <TextInput
                      id="text" type="text"
                      placeholder="새로운 소식이 있나요?"
                      onClick={() => setOpenModal(true)}
                      readOnly
                  />
                </div>
                <Button className="!bg-blue-900 hover:!bg-blue-800" onClick={() => setOpenModal(true)}>게시</Button>
              </div>
            </li>
          </ul>
          <ModalBoardForm open={openModal} onClose={closeModalHandler} />
        </div>
      </Card>
  );
}
