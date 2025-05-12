import { useState } from "react";

export function ImageComponent() {
  const [currentSlide, setCurrentSlide] = useState(0);
  const images = [
    "https://picsum.photos/200",
    "https://picsum.photos/300",
    "https://picsum.photos/400",
  ];

  const handleNext = () => {
    setCurrentSlide((prev) => (prev + 1) % images.length);
  };

  const handlePrev = () => {
    setCurrentSlide((prev) =>
        prev === 0 ? images.length - 1 : prev - 1
    );
  };

  return (
      <div className="relative w-full max-w-md mx-auto overflow-hidden">
        <div className="w-full h-64 flex items-center justify-center">
          <img
              src={images[currentSlide]}
              alt={`Slide ${currentSlide + 1}`}
              className="w-full h-full object-cover"
          />
        </div>
        <button
            className="absolute top-1/2 left-4 transform -translate-y-1/2 text-white bg-gray-800 p-2 rounded-full"
            onClick={handlePrev}
        >
          ❮
        </button>
        <button
            className="absolute top-1/2 right-4 transform -translate-y-1/2 text-white bg-gray-800 p-2 rounded-full"
            onClick={handleNext}
        >
          ❯
        </button>
      </div>
  );
}
