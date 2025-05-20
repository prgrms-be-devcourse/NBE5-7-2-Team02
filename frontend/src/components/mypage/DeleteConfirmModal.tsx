import { Modal, Button } from "flowbite-react";

interface DeleteConfirmModalProps {
  show: boolean;
  onCancel: () => void;
  onConfirm: () => void;
}

export const DeleteConfirmModal = ({
                                     show,
                                     onCancel,
                                     onConfirm,
                                   }: DeleteConfirmModalProps) => {
  return (
    <Modal
      show={show}
      size="sm"
      onClose={onCancel}
      position="center"
    >
      <div className="px-6 py-8 text-center">
        <p className="text-base text-gray-700 dark:text-gray-300 mb-8">
          삭제하면 복구할 수 없습니다. <br />
          정말 삭제하시겠습니까?
        </p>

        <div className="flex justify-between px-4">
          <Button color="gray" onClick={onCancel}>
            취소
          </Button>
          <Button className="text-red-600" color="light" onClick={onConfirm}>
            삭제
          </Button>
        </div>
      </div>
    </Modal>
  );
};
