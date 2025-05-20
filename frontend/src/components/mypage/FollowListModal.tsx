import { Modal, Avatar } from "flowbite-react";

interface FollowUser {
  id: number;
  username: string;
  profileImage: string;
}

interface Props {
  show: boolean;
  onClose: () => void;
  title: string;
  users: FollowUser[];
  onProfileClick: (memberId: number) => void;
}

export default function FollowListModal({
  show,
  onClose,
  title,
  users,
  onProfileClick,
}: Props) {
  return (
    <Modal show={show} onClose={onClose} position="center" dismissible>
      <div className="p-6 max-w-md mx-auto">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-bold">{title}</h3>
        </div>
        {users.length === 0 ? (
          <p className="text-center text-gray-500">{title}가 없습니다.</p>
        ) : (
          <ul className="space-y-3">
            {users.map((u) => (
              <li
                key={u.id}
                className="flex items-center gap-3 cursor-pointer"
                onClick={() => onProfileClick(u.id)}
              >
                <Avatar img={u.profileImage} size="sm" />
                <span className="font-medium">{u.username}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </Modal>
  );
}
