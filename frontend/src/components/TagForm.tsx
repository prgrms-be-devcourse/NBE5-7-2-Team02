import React, { useState } from "react";
import { TextInput, Badge } from "flowbite-react";
import { HiXCircle } from "react-icons/hi";

interface TagFormProps {
  onTagsChange?: (tags: string[]) => void;
}

export const TagForm: React.FC<TagFormProps> = ({ onTagsChange }) => {
  const [tagInput, setTagInput] = useState("");
  const [tags, setTags] = useState<string[]>([]);

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
      </div>
  );
};
