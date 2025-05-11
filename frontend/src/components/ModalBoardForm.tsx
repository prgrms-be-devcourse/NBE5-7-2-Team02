import React, { useState } from "react";
import {
  Button, FileInput,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Textarea,
} from "flowbite-react";
import { TagForm } from "./TagForm";

interface ModalComponentProps {
  open: boolean;
  onClose: () => void;
}

export const ModalBoardForm: React.FC<ModalComponentProps> = ({ open, onClose }) => {
  const [tags, setTags] = useState<string[]>([]);

  const handleSubmit = () => {
    console.log("Submitted tags:", tags);
    onClose();
  };

  return (
      <Modal show={open} onClose={onClose}>
        <ModalHeader>게시글 작성</ModalHeader>
        <ModalBody>
          <div className="space-y-4">
            <Textarea id="comment" placeholder="Leave a comment..." required rows={4} />
            <FileInput id="file" multiple={true}/>

          </div>
        </ModalBody>
        <ModalFooter className="flex flex-col gap-2">
          <div className="w-full">
            <TagForm onTagsChange={(updatedTags) => setTags(updatedTags)} />
          </div>
          <div className="flex justify-end space-x-2 w-full">
            <Button className="!bg-blue-900 hover:!bg-blue-800" onClick={handleSubmit}>작성</Button>
            <Button color="gray" onClick={onClose}>취소</Button>
          </div>
        </ModalFooter>
      </Modal>
  );
};
