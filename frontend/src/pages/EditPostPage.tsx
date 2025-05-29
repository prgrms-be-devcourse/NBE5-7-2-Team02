import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Label,
  TextInput,
  Textarea,
  Button,
  FileInput,
} from "flowbite-react";
import api from "../api/axiosInstance";
import { TagForm } from "../components/TagForm";

export default function EditPostPage() {
  const { postId } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [recruitmentStatus, setRecruitmentStatus] = useState("NONE");
  const [tags, setTags] = useState<string[]>([]);
  const [images, setImages] = useState<File[]>([]);

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const res = await api.get(`/posts/${postId}`);
        const data = res.data;

        if (!data) {
          console.error("게시글 데이터 없음");
          return;
        }

        setTitle(data.title ?? "");
        setContent(data.content ?? "");
        setRecruitmentStatus(data.recruitment_status?.toUpperCase() ?? "NONE");
        setTags(data.tags ?? []);
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
      setImages((prev) => [...prev, ...newFiles].slice(0, 10));
    }
  };

  const handleSubmit = async () => {
    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    formData.append("recruitmentStatus", recruitmentStatus);
    tags.forEach((tag) => formData.append("tags", tag));
    images.forEach((file) => formData.append("images", file));

    try {
      await api.patch(`/posts/${postId}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      alert("게시글이 수정되었습니다.");
      navigate("/mypage");
    } catch (error) {
      console.error("게시글 수정 실패:", error);
    }
  };

  return (
    <div className="max-w-3xl mx-auto p-4">
      <h2 className="text-xl font-bold mb-4">게시글 수정</h2>

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

        <div>
          <Label htmlFor="file">이미지 추가</Label>
          <FileInput id="file" multiple onChange={handleFileChange} />
        </div>

        <TagForm externalTags={tags} onTagsChange={setTags} />

        <div className="h-8" />

        <div className="flex justify-between items-center mt-4">
          <Button type="button" color="gray" onClick={() => navigate(-1)}>
            취소
          </Button>
          <Button type="button" color="blue" onClick={handleSubmit}>
            수정 완료
          </Button>
        </div>
      </div>
    </div>
  );
}
