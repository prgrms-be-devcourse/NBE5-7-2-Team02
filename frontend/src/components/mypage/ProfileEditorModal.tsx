import { Modal } from "flowbite-react";
import ProfileEditor from "./ProfileEditor";

interface Props {
  show: boolean;
  onClose: () => void;
  username: string;
  profileImage: string;
  onSave: (nickname: string, image: File | null) => void; // ← 타입 변경됨
}

export default function ProfileEditorModal({
  show,
  onClose,
  username,
  profileImage,
  onSave,
}: Props) {
  return (
    <Modal show={show} onClose={onClose} position="center">
      <div className="p-6">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-xl font-bold">프로필 편집</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-700 text-xl">
            &times;
          </button>
        </div>
        <ProfileEditor
          username={username}
          profileImage={profileImage}
          onSave={onSave}
          onCancel={onClose}
        />
      </div>
    </Modal>
  );
}
