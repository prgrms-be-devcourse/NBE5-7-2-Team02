import { Modal, Button } from "flowbite-react";

interface AlertModalProps {
  show: boolean;
  onClose: () => void;
  message: string;
  buttonText?: string;
}

export const AlertModal = ({
  show,
  onClose,
  message,
  buttonText = "확인",
}: AlertModalProps) => {
  const lines = message.split("\n");

  return (
    <Modal show={show} size="md" onClose={onClose} position="center">
      <div className="px-6 py-8 text-center">
        <div className="mx-auto mb-8 max-w-[480px] text-base text-gray-700 dark:text-gray-300">
          {lines.map((line, idx) => (
            <p key={idx} className="mb-2">
              {line}
            </p>
          ))}
        </div>
        <div className="flex justify-center">
          <Button color="gray" onClick={onClose}>
            {buttonText}
          </Button>
        </div>
      </div>
    </Modal>
  );
};
