import React, { useState } from "react";
import {
  Button,
  FileInput,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Textarea,
} from "flowbite-react";
import { TagForm } from "./TagForm";
import api from "../api/axiosInstance.ts";

interface ModalComponentProps {
  open: boolean;
  onClose: () => void;
}

export const ModalBoardForm = ({ open, onClose }: ModalComponentProps) => {
  const [tags, setTags] = useState<string[]>([]);
  const [text, setText] = useState<string>("");
  const [files, setFiles] = useState<FileList | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFiles(e.target.files);
  };

  const handleSubmit = async () => {
    const formData = new FormData();
    formData.append("text", text);
    tags.forEach((tag) => formData.append("tags", tag));
    if (files) {
      Array.from(files).forEach((file) => formData.append("files", file));
    }

    try {
      await api.post("/posts", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      console.log("Post request sent successfully.");
      onClose();
    } catch (error) {
      console.error("Error sending post request:", error);
    }
  };

  return (
      <Modal show={open} onClose={onClose}>
        <ModalHeader>게시글 작성</ModalHeader>
        <ModalBody>
          <div className="space-y-4">
            <Textarea
                id="comment"
                placeholder="Leave a comment..."
                required
                rows={4}
                value={text}
                onChange={(e) => setText(e.target.value)}
            />
            <FileInput id="file" multiple={true} onChange={handleFileChange} />
            <TagForm onTagsChange={(updatedTags) => setTags(updatedTags)} />
          </div>
        </ModalBody>
        <ModalFooter className="flex flex-col gap-2">
          <div className="flex justify-end space-x-2 w-full">
            <Button className="!bg-blue-900 hover:!bg-blue-800" onClick={handleSubmit}>
              작성
            </Button>
            <Button color="gray" onClick={onClose}>취소</Button>
          </div>
        </ModalFooter>
      </Modal>
  );
};
