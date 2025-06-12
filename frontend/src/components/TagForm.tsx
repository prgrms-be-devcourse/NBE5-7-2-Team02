import { useState, useEffect } from "react";
import { TextInput, Badge } from "flowbite-react";
import { HiXCircle } from "react-icons/hi";
import api from "../api/axiosInstance";

interface TagFormProps {
  externalTags?: string[];
  onTagsChange?: (tags: string[]) => void;
}

export const TagForm = ({ externalTags = [], onTagsChange }: TagFormProps) => {

  const [tagInput, setTagInput] = useState("");
  const [tags, setTags] = useState<string[]>([]);
  const [allTags, setAllTags] = useState<string[]>([]); // ✅ 전체 태그 목록
  const [suggestedTags, setSuggestedTags] = useState<string[]>([]);

  // ✅ 부모 컴포넌트로부터 태그 변경 감지
  useEffect(() => {
    setTags(externalTags);
  }, [externalTags]);

  useEffect(() => {
    // ✅ 전체 태그 목록 API 로드 (초기 1회만)
    const fetchTags = async () => {
      try {
        const response = await api.get("/tags");
        if (response.status === 200) {
          setAllTags(response.data.tags);
        }
      } catch (error) {
        console.error("Error fetching all tags:", error);
      }
    };

    fetchTags();
  }, []);

  const addTag = (tag: string) => {
    if (tag.trim() && !tags.includes(tag.trim())) {
      const updatedTags = [...tags, tag.trim()];
      setTags(updatedTags);
      if (onTagsChange) onTagsChange(updatedTags);
    }
  };

  const removeTag = (tag: string) => {
    const updatedTags = tags.filter((t) => t !== tag);
    setTags(updatedTags);
    if (onTagsChange) onTagsChange(updatedTags);
  };

  // ✅ 입력에 따라 추천 태그 필터링
  useEffect(() => {
    if (tagInput.trim().length === 0) {
      setSuggestedTags([]);
    } else {
      const filteredTags = allTags.filter((tag) =>
          tag.toLowerCase().includes(tagInput.trim().toLowerCase())
      ).slice(0, 10); // 최대 10개의 추천 태그
      setSuggestedTags(filteredTags);
    }
  }, [tagInput, allTags]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTagInput(e.target.value);
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" || e.key === " ") {
      addTag(tagInput);
      setTagInput("");
      e.preventDefault();
    }
  };

  return (
      <div className="flex flex-col gap-2">
        <TextInput
            addon="#"
            value={tagInput}
            onChange={handleInputChange}
            onKeyPress={handleKeyPress}
            placeholder="태그를 입력해주세요"
        />

        {/* 🔵 선택된 태그 목록 */}
        <div className="flex flex-wrap gap-2 mt-2">
          {tags.map((tag, index) => (
              <Badge key={index} size="sm" color="gray" className="flex items-center gap-2">
                <div className="flex items-center gap-1">
                  <HiXCircle className="cursor-pointer" onClick={() => removeTag(tag)} />
                  <span>{tag}</span>
                </div>
              </Badge>
          ))}
        </div>

        {/* ✅ 추천 태그 */}
        {suggestedTags.length > 0 && (
            <div className="flex flex-wrap gap-2 mt-2">
              {suggestedTags.map((suggestedTag, index) => (
                  <Badge
                      key={index}
                      size="sm"
                      color="info"
                      className="cursor-pointer"
                      onClick={() => {
                        addTag(suggestedTag);
                        setTagInput(""); // 선택 시 입력란 초기화
                      }}
                  >
                    {suggestedTag}
                  </Badge>
              ))}
            </div>
        )}
      </div>
  );
};
