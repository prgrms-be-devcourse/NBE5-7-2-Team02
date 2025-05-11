// src/components/CardItem.tsx
import React from "react";
import {Badge, Card} from "flowbite-react";
import {ImageComponent} from "./ImageComponent.tsx";

interface CardItemProps {
  title: string;
  description: string;
  href?: string;
}

const CardItem: React.FC<CardItemProps> = ({ title, description, href }) => {
  return (
      <Card href={href} className="w-full dark:border-gray-600 dark:!bg-dark">
        <h5 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          {title}
        </h5>
        <p className="font-normal text-gray-700 dark:text-gray-400">
          {description}
        </p>
        <div className="w-full">
          <ImageComponent />
        </div>
        <div className="flex flex-wrap gap-2">
          <Badge size="sm" color="gray">스터디</Badge>
          <Badge size="sm" color="gray">백엔드</Badge>
          <Badge size="sm" color="gray">모각코</Badge>
        </div>
      </Card>
  );
};

export default CardItem;
