import React, {useState} from "react";
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
import {TagForm} from "./TagForm";
import api from "../api/axiosInstance.ts";

interface ModalComponentProps {
  open: boolean;
  onClose: () => void;
}

export const ModalBoardForm = ({open, onClose}: ModalComponentProps) => {
  const [tags, setTags] = useState<string[]>([]);
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");
  const [recruitmentStatus, setRecruitmentStatus] = useState<string>("NONE");
  const [files, setFiles] = useState<File[]>([]);

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
    window.location.reload();
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selected = e.target.files;
    if (selected) {
      const newFiles = Array.from(selected);
      setFiles((prev) => [...prev, ...newFiles].slice(0, 10)); // 최대 10개 누적
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
      console.log("Post created successfully.");
      handleClose();
    } catch (error) {
      console.error("Error creating post:", error);
    }
  };

  return (
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

            <div className="flex items-center gap-4 mt-2 text-sm">
              <Label className="whitespace-nowrap">모집 상태</Label>
              <div className="flex gap-4">
                <label className="flex items-center gap-1">
                  <input
                      type="radio"
                      name="recruitmentStatus"
                      value="NONE"
                      checked={recruitmentStatus === "NONE"}
                      onChange={(e) => setRecruitmentStatus(e.target.value)}
                  />
                  선택 안 함
                </label>
                <label className="flex items-center gap-1">
                  <input
                      type="radio"
                      name="recruitmentStatus"
                      value="RECRUITING"
                      checked={recruitmentStatus === "RECRUITING"}
                      onChange={(e) => setRecruitmentStatus(e.target.value)}
                  />
                  모집 중
                </label>
                <label className="flex items-center gap-1">
                  <input
                      type="radio"
                      name="recruitmentStatus"
                      value="DONE"
                      checked={recruitmentStatus === "DONE"}
                      onChange={(e) => setRecruitmentStatus(e.target.value)}
                  />
                  모집 마감
                </label>
              </div>
            </div>

            <div>
              <Label htmlFor="file">이미지 (10개까지만 첨부됩니다)</Label>
              <FileInput id="file" multiple={true} onChange={handleFileChange}/>
              <ul className="mt-2 text-sm text-gray-600">
                {files.map((file, idx) => (
                    <li key={idx}>📎 {file.name}</li>
                ))}
              </ul>
            </div>

            <TagForm
                externalTags={tags}
                onTagsChange={(updatedTags) => setTags(updatedTags)}
            />
          </div>
        </ModalBody>
        <ModalFooter className="flex justify-end space-x-2">
          <Button className="!bg-blue-900 hover:!bg-blue-800" onClick={handleSubmit}>
            작성
          </Button>
          <Button color="gray" onClick={handleClose}>
            취소
          </Button>
        </ModalFooter>
      </Modal>
  );
};