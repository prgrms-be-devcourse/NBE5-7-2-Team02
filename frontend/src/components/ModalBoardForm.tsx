import React, { useState } from "react";
import {
  Button,
  FileInput,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Textarea,
  Label,
  TextInput,
} from "flowbite-react";
import { TagForm } from "./TagForm";
import api from "../api/axiosInstance";
import { ImageLimitModal } from "./ImageLimitModal"; // 모달 추가

interface ModalComponentProps {
  open: boolean;
  onClose: () => void;
}

export const ModalBoardForm = ({ open, onClose }: ModalComponentProps) => {
  const [tags, setTags] = useState<string[]>([]);
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");
  const [recruitmentStatus, setRecruitmentStatus] = useState<string>("NONE");
  const [files, setFiles] = useState<File[]>([]);
  const [showLimitModal, setShowLimitModal] = useState(false); // 모달 상태

  const resetForm = () => {
    setTags([]);
    setTitle("");
    setContent("");
    setRecruitmentStatus("NONE");
    setFiles([]);
  };

  const handleClose = () => {
    resetForm();
    onClose();
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selected = e.target.files;
    if (selected) {
      const newFiles = Array.from(selected);
      const totalFiles = files.length + newFiles.length;
      if (totalFiles > 10) {
        setFiles([]);
        e.target.value = "";
        setShowLimitModal(true);
      } else {
        setFiles((prev) => [...prev, ...newFiles]);
      }
    }
  };

  const handleSubmit = async () => {
    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    formData.append("recruitmentStatus", recruitmentStatus);
    tags.forEach((tag) => formData.append("tags", tag));
    files.forEach((file) => formData.append("images", file));

    try {
      await api.post("/posts", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      window.location.reload();
      handleClose();
    } catch (error) {
      console.error("Error creating post:", error);
    }
  };

  return (
    <>
      <Modal show={open} onClose={handleClose}>
        <ModalHeader>게시글 작성</ModalHeader>
        <ModalBody>
          <div className="space-y-4">
            <div>
              <Label htmlFor="title">제목</Label>
              <TextInput
                id="title"
                placeholder="제목을 입력해주세요"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>

            <div>
              <Label htmlFor="content">내용</Label>
              <Textarea
                id="content"
                placeholder="내용을 입력해주세요"
                value={content}
                rows={4}
                onChange={(e) => setContent(e.target.value)}
                required
              />
            </div>

            <div className="mt-2 flex items-center gap-4 text-sm">
              <Label className="whitespace-nowrap">모집 상태</Label>
              <div className="flex gap-4">
                {["NONE", "RECRUITING", "DONE"].map((status) => (
                  <label key={status} className="flex items-center gap-1">
                    <input
                      type="radio"
                      name="recruitmentStatus"
                      value={status}
                      checked={recruitmentStatus === status}
                      onChange={(e) => setRecruitmentStatus(e.target.value)}
                    />
                    {status === "NONE"
                      ? "선택 안 함"
                      : status === "RECRUITING"
                        ? "모집 중"
                        : "모집 마감"}
                  </label>
                ))}
              </div>
            </div>

            <div>
              <Label htmlFor="file">
                이미지 (최대 10장까지만 업로드할 수 있습니다)
              </Label>
              <FileInput id="file" multiple onChange={handleFileChange} />
              <ul className="mt-2 text-sm text-gray-600">
                {files.map((file, idx) => (
                  <li key={idx}>📎 {file.name}</li>
                ))}
              </ul>
            </div>

            <TagForm externalTags={tags} onTagsChange={setTags} />
          </div>
        </ModalBody>
        <ModalFooter className="flex justify-end space-x-2">
          <Button
            className="!bg-blue-900 hover:!bg-blue-800"
            onClick={handleSubmit}
          >
            작성
          </Button>
          <Button color="gray" onClick={handleClose}>
            취소
          </Button>
        </ModalFooter>
      </Modal>

      {/* 이미지 제한 모달 */}
      <ImageLimitModal
        show={showLimitModal}
        onClose={() => setShowLimitModal(false)}
      />
    </>
  );
};
