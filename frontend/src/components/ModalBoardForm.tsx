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
import api from "../api/axiosInstance.ts";
import { AlertModal } from "./AlertModal";

interface ModalComponentProps {
  open: boolean;
  onClose: () => void;
}

export const ModalBoardForm = ({ open, onClose }: ModalComponentProps) => {
  const [tags, setTags] = useState<string[]>([]);
  const [title, setTitle] = useState<string>("");
  const [content, setContent] = useState<string>("");
  const [recruitmentStatus, setRecruitmentStatus] = useState<string>("NONE");
  const [recruitmentDeadline, setRecruitmentDeadline] = useState<string>("");
  const [recruitmentFields, setRecruitmentFields] = useState([
    { fieldName: "", totalCount: 1 },
  ]);
  const [files, setFiles] = useState<File[]>([]);

  const [showAlertModal, setShowAlertModal] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");

  const resetForm = () => {
    setTags([]);
    setTitle("");
    setContent("");
    setRecruitmentStatus("NONE");
    setRecruitmentDeadline("");
    setRecruitmentFields([{ fieldName: "", totalCount: 1 }]);
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
        setAlertMessage(
          "이미지는 최대 10장까지만 업로드할 수 있습니다.\n다시 선택해주세요.",
        );
        setShowAlertModal(true);
      } else {
        setFiles((prev) => [...prev, ...newFiles]);
      }
    }
  };

  const handleSubmit = async () => {
    const today = new Date();
    const deadlineDate = new Date(recruitmentDeadline);

    today.setHours(0, 0, 0, 0);
    deadlineDate.setHours(0, 0, 0, 0);

    if (
      recruitmentStatus === "RECRUITING" &&
      recruitmentDeadline &&
      deadlineDate < today
    ) {
      setAlertMessage("마감일은 작성일 이후의 날짜여야 합니다.");
      setShowAlertModal(true);
      return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    formData.append("recruitmentStatus", recruitmentStatus);

    if (recruitmentStatus === "RECRUITING" && recruitmentDeadline) {
      formData.append("recruitmentDeadline", recruitmentDeadline);
    }

    tags.forEach((tag) => formData.append("tags", tag));
    formData.append(
      "recruitmentFieldsJson",
      JSON.stringify(
        recruitmentStatus === "RECRUITING" ? recruitmentFields : [],
      ),
    );

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

            {recruitmentStatus === "RECRUITING" && (
              <div className="space-y-2">
                <Label>모집 마감일</Label>
                <TextInput
                  type="date"
                  value={recruitmentDeadline}
                  onChange={(e) => setRecruitmentDeadline(e.target.value)}
                  min={new Date().toISOString().split("T")[0]}
                />

                <Label>모집 분야별 인원</Label>
                {recruitmentFields.map((field, idx) => (
                  <div key={idx} className="flex gap-2">
                    <TextInput
                      placeholder="분야명 (예: 프론트엔드)"
                      value={field.fieldName}
                      onChange={(e) =>
                        setRecruitmentFields((prev) =>
                          prev.map((f, i) =>
                            i === idx ? { ...f, fieldName: e.target.value } : f,
                          ),
                        )
                      }
                    />
                    <TextInput
                      type="number"
                      min={1}
                      placeholder="모집 인원"
                      value={field.totalCount}
                      onChange={(e) =>
                        setRecruitmentFields((prev) =>
                          prev.map((f, i) =>
                            i === idx
                              ? {
                                  ...f,
                                  totalCount: Number(e.target.value),
                                }
                              : f,
                          ),
                        )
                      }
                    />
                    <Button
                      color="gray"
                      onClick={() =>
                        setRecruitmentFields((prev) =>
                          prev.filter((_, i) => i !== idx),
                        )
                      }
                    >
                      삭제
                    </Button>
                  </div>
                ))}
                <Button
                  size="sm"
                  className="bg-blue-700 text-white hover:bg-blue-800"
                  onClick={() =>
                    setRecruitmentFields((prev) => [
                      ...prev,
                      { fieldName: "", totalCount: 1 },
                    ])
                  }
                >
                  분야 추가
                </Button>
              </div>
            )}

            <div>
              <Label htmlFor="file">
                이미지 (최대 10장까지만 업로드할 수 있습니다)
              </Label>
              <FileInput
                id="file"
                multiple={true}
                onChange={handleFileChange}
              />
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

      <AlertModal
        show={showAlertModal}
        onClose={() => setShowAlertModal(false)}
        message={alertMessage}
      />
    </>
  );
};
