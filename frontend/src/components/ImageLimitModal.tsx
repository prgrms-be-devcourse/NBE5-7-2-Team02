import { Modal, Button } from "flowbite-react";

interface ImageLimitModalProps {
  show: boolean;
  onClose: () => void;
}

export const ImageLimitModal = ({ show, onClose }: ImageLimitModalProps) => {
  return (
    <Modal show={show} size="md" onClose={onClose} position="center">
      <div className="px-6 py-8 text-center">
        <p className="mb-2 text-base whitespace-nowrap text-gray-700 dark:text-gray-300">
          이미지는 최대 10장까지만 업로드할 수 있습니다.
        </p>
        <p className="mb-8 text-base text-gray-700 dark:text-gray-300">
          다시 선택해주세요.
        </p>

        <div className="flex justify-center">
          <Button color="gray" onClick={onClose}>
            확인
          </Button>
        </div>
      </div>
    </Modal>
  );
};
