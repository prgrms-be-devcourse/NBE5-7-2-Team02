import React, { useState } from "react";
import { TextInput, Badge } from "flowbite-react";
import { HiXCircle } from "react-icons/hi";
import api from "../api/axiosInstance.ts";

interface TagFormProps {
  onTagsChange?: (tags: string[]) => void;
}

export const TagForm = ({ onTagsChange }: TagFormProps) => {
  const [tagInput, setTagInput] = useState("");
  const [tags, setTags] = useState<string[]>([]);
  const [suggestedTags, setSuggestedTags] = useState<string[]>([]);

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

  const handleInputChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const input = e.target.value;
    setTagInput(input);

    if (input.trim().length === 0) {
      setSuggestedTags([]);
      return;
    }

    try {
      const response = await api.get("/tags", {
        params: { query: input },
      });

      // ✅ 204 No Content인 경우 추천 태그 목록을 초기화
      if (response.status === 204) {
        setSuggestedTags([]);
        return;
      }

      // ✅ 200 OK일 때만 추천 태그 목록을 업데이트
      if (response.status === 200) {
        setSuggestedTags(response.data.data.tags);
      }
    } catch (error) {
      console.error("Error fetching tags:", error);
    }
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
                      onClick={() => addTag(suggestedTag)}
                  >
                    {suggestedTag}
                  </Badge>
              ))}
            </div>
        )}
      </div>
  );
};
