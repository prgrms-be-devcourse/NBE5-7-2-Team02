import { ToggleGroup, ToggleGroupItem } from "../components/ui/toggle-group";
import { Switch } from "../components/ui/switch";
import { Label } from "../components/ui/label";
import { Button } from "../components/ui/button";
import { Filter, Users } from "lucide-react";
import { useAuth } from "../context/AuthContext";

interface FilterOptionsProps {
  isRecruit: boolean | null;
  isFollowing: boolean;
  onRecruitChange: (value: boolean | null) => void;
  onFollowingChange: (value: boolean) => void;
  onReset: () => void;
}

export const FilterOptions: React.FC<FilterOptionsProps> = ({
                                                              isRecruit,
                                                              isFollowing,
                                                              onRecruitChange,
                                                              onFollowingChange,
                                                              onReset,
                                                            }) => {
  const { isAuthenticated } = useAuth();
  return (
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-4 p-4 bg-card dark:bg-dark rounded-lg border">
        {/* 모집 여부 필터 (ToggleGroup 사용) */}
        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-2 text-sm font-medium">
            <Filter className="h-4 w-4" />
            <span>모집 상태</span>
          </div>
          <ToggleGroup
              type="single"
              value={isRecruit === null ? "all" : isRecruit ? "recruiting" : "closed"}
              onValueChange={(value) => {
                if (value === "") return; // 선택 해제 방지
                const recruitValue = value === "all" ? null : value === "recruiting";
                onRecruitChange(recruitValue);
              }}
              className="bg-muted/30"
          >
            <ToggleGroupItem value="all" aria-label="모든 게시글" className="text-sm">
              전체
            </ToggleGroupItem>
            <ToggleGroupItem value="recruiting" aria-label="모집중인 게시글" className="text-sm">
              모집중
            </ToggleGroupItem>
            <ToggleGroupItem value="closed" aria-label="모집 마감된 게시글" className="text-sm">
              마감
            </ToggleGroupItem>
          </ToggleGroup>
        </div>

        {/* 필터 컨트롤 영역 */}
        <div className="flex items-center gap-4">
          {/* 팔로잉 필터 (Switch 사용) */}
          {isAuthenticated?
            <div className="flex items-center gap-2">
              <div className="flex items-center gap-2">
                <Users className="h-4 w-4" />
                <Label htmlFor="following-filter" className="text-sm font-medium cursor-pointer">팔로잉한 사용자만</Label>
              </div>
              <Switch
                  id="following-filter"
                  checked={isFollowing}
                  onCheckedChange={onFollowingChange}
              />
            </div>
          : null }
          {/* 초기화 버튼 */}
          <Button
              variant="outline"
              size="sm"
              onClick={onReset}
              className="text-sm"
          >
            초기화
          </Button>
        </div>
      </div>
  );
};