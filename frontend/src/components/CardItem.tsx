import { Post } from "../types/Post";
import {Badge, Card} from "flowbite-react";
import {ImageComponent} from "./ImageComponent.tsx";

interface CardItemProps {
  post: Post;
}

export const CardItem = ({ post }: CardItemProps) => {
  return (
      <Card className="w-full dark:border-gray-600 dark:!bg-dark">
        <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          {post.title}
        </h5>
        <p className="font-normal text-gray-700 dark:text-gray-400">
          {post.content}
        </p>
        <div className="w-full">
          <ImageComponent />
        </div>
        <div className="flex flex-wrap gap-2">
           {post.tags.map((tag, index) => (
               <Badge size="sm" color="gray" key={index}>{tag}</Badge>
           ))}
        </div>
      </Card>
  );
};
