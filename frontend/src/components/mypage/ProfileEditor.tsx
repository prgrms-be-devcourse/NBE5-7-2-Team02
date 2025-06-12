import { useState } from "react";
import { Avatar, Button } from "flowbite-react";

interface Props {
  username: string;
  profileImage: string;
  onSave: (nickname: string, image: File | null) => void;
  onCancel: () => void;
}

export default function ProfileEditor({ username, profileImage, onSave, onCancel }: Props) {
  const [nickname, setNickname] = useState(username);
  const [preview, setPreview] = useState(profileImage);
  const [selectedFile, setSelectedFile] = useState<File | null>(null); // 파일 상태 추가

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null;
    setSelectedFile(file);

    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => setPreview(reader.result as string);
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="space-y-4 text-center">
      <Avatar img={preview} size="xl" className="mx-auto" />

      <div className="flex flex-col items-center">
        <input
          type="file"
          accept="image/*"
          onChange={handleImageChange}
          className="text-sm text-gray-400 file:bg-gray-700 file:text-white file:rounded file:border-0 file:px-4 file:py-1 file:cursor-pointer"
        />
      </div>

      <input
        type="text"
        value={nickname}
        onChange={(e) => setNickname(e.target.value)}
        className="w-full border rounded p-2 bg-gray-800 text-white"
        placeholder="닉네임 입력"
      />

      <div className="flex justify-center gap-3 pt-2">
        <Button className="bg-blue-300 hover:bg-blue-500" onClick={() => onSave(nickname, selectedFile)}>저장</Button>
        <Button color="gray" onClick={onCancel}>취소</Button>
      </div>
    </div>
  );
}
