import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Label, TextInput, Textarea, Button, FileInput } from "flowbite-react";
import api from "../api/axiosInstance";
import { TagForm } from "../components/TagForm";
import { AlertModal } from "../components/AlertModal";

export default function EditPostPage() {
  const { postId } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [recruitmentStatus, setRecruitmentStatus] = useState("NONE");
  const [recruitDeadline, setRecruitDeadline] = useState("");
  const [recruitmentFields, setRecruitmentFields] = useState([
    { fieldName: "", totalCount: 1 },
  ]);
  const [tags, setTags] = useState<string[]>([]);
  const [files, setFiles] = useState<File[]>([]);

  const [showAlertModal, setShowAlertModal] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await api.get(`/posts/${postId}`);
        const data = res.data;

        if (!data) return;

        setTitle(data.title ?? "");
        setContent(data.content ?? "");
        setRecruitmentStatus(data.recruitment_status?.toUpperCase() ?? "NONE");
        setTags(data.tags ?? []);

        if (data.recruitment_deadline) {
          setRecruitDeadline(data.recruitment_deadline);
        }

        if (data.recruitment_fields && data.recruitment_fields.length > 0) {
          setRecruitmentFields(
            data.recruitment_fields.map((f: any) => ({
              fieldName: f.field_name,
              totalCount: f.total_count,
            })),
          );
        }
      } catch (error) {
        console.error("게시글 불러오기 실패:", error);
      }
    };

    fetchPost();
  }, [postId]);

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
    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    formData.append("recruitmentStatus", recruitmentStatus);

    if (recruitmentStatus === "RECRUITING") {
      formData.append("recruitDeadline", recruitDeadline);
      formData.append("recruitmentFields", JSON.stringify(recruitmentFields));
    }

    tags.forEach((tag) => formData.append("tags", tag));
    files.forEach((file) => formData.append("images", file));

    try {
      await api.patch(`/posts/${postId}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setAlertMessage("게시글이 수정되었습니다.");
      setShowAlertModal(true);
      setTimeout(() => navigate("/mypage"), 1000);
    } catch (error) {
      console.error("게시글 수정 실패:", error);
    }
  };

  return (
    <div className="mx-auto max-w-3xl p-4">
      <h2 className="mb-4 text-xl font-bold">게시글 수정</h2>

      <div className="space-y-4">
        <div>
          <Label htmlFor="title">제목</Label>
          <TextInput
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </div>

        <div>
          <Label htmlFor="content">내용</Label>
          <Textarea
            id="content"
            value={content}
            rows={4}
            onChange={(e) => setContent(e.target.value)}
          />
        </div>

        <div className="flex items-center gap-4 text-sm">
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
              value={recruitDeadline}
              onChange={(e) => setRecruitDeadline(e.target.value)}
            />
            <Label>모집 분야별 인원</Label>
            {recruitmentFields.map((field, idx) => (
              <div key={idx} className="flex gap-2">
                <TextInput
                  placeholder="분야명"
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
                  placeholder="인원 수"
                  value={field.totalCount}
                  onChange={(e) =>
                    setRecruitmentFields((prev) =>
                      prev.map((f, i) =>
                        i === idx
                          ? { ...f, totalCount: Number(e.target.value) }
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
              className="!bg-blue-900 hover:!bg-blue-800"
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
          <FileInput id="file" multiple onChange={handleFileChange} />
        </div>

        <TagForm externalTags={tags} onTagsChange={setTags} />

        <div className="h-8" />

        <div className="mt-4 flex items-center justify-between">
          <Button type="button" color="gray" onClick={() => navigate(-1)}>
            취소
          </Button>
          <Button type="button" color="blue" onClick={handleSubmit}>
            수정 완료
          </Button>
        </div>
      </div>

      <AlertModal
        show={showAlertModal}
        onClose={() => setShowAlertModal(false)}
        message={alertMessage}
      />
    </div>
  );
}
