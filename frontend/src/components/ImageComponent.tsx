import { useState, useEffect } from "react";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
  type CarouselApi,
} from "@/components/ui/carousel";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { Dialog, DialogContent } from "@/components/ui/dialog";

interface ImageCarouselProps {
  images: string[];
  className?: string;
}

export const ImageComponent = ({
                                images,
                                className
                              }: ImageCarouselProps) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isHovering, setIsHovering] = useState(false);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);

  const [api, setApi] = useState<CarouselApi | undefined>()

  useEffect(() => {
    if (!api) return

    const onSelect = () => {
      const selected = api.selectedScrollSnap()
      setCurrentIndex(selected)
    }

    api.on("select", onSelect)
    onSelect() // 초기 상태 설정

    return () => {
      api.off("select", onSelect) // cleanup
    }
  }, [api])

  // Only show controls if there's more than one image
  const showControls = images.length > 1;

  // Handle image click to open dialog
  const handleImageClick = (src: string) => {
    setSelectedImage(src);
  };

  return (
      <>
        <div
            className={cn("relative w-full group", className)}
            onMouseEnter={() => setIsHovering(true)}
            onMouseLeave={() => setIsHovering(false)}
        >
          <Carousel
              opts={{ loop: true }}
              setApi={setApi}
              className="w-full"
              // onSelect={handleScroll}
          >
            <CarouselContent>
              {images.map((src, index) => (
                  <CarouselItem key={index}>
                    <div
                        className="h-64 w-full overflow-hidden rounded-md cursor-pointer"
                        onClick={() => handleImageClick(src)}
                    >
                      <img
                          src={`${import.meta.env.VITE_BASE_URL}${src}`}
                          alt={`Image ${index + 1}`}
                          className="h-full w-full object-contain bg-muted/20"
                          style={{ objectFit: 'contain' }}
                      />
                    </div>
                  </CarouselItem>
              ))}
            </CarouselContent>

            {showControls && (
                <>
                  <CarouselPrevious
                      className={cn(
                          "absolute left-2 top-1/2 transform -translate-y-1/2 bg-background/80 hover:bg-background/90 transition-opacity",
                          (isHovering) ? "opacity-100" : "opacity-0 group-hover:opacity-100"
                      )}
                  />
                  <CarouselNext
                      className={cn(
                          "absolute right-2 top-1/2 transform -translate-y-1/2 bg-background/80 hover:bg-background/90 transition-opacity",
                          (isHovering) ? "opacity-100" : "opacity-0 group-hover:opacity-100"
                      )}
                  />
                </>
            )}
          </Carousel>

          {/* Dot indicators */}
          {showControls && (
              <div className="absolute bottom-2 left-0 right-0 flex justify-center gap-1 transition-opacity">
                {images.map((_, index) => (
                    <Button
                        key={index}
                        variant="ghost"
                        size="icon"
                        className={cn(
                            "h-2 w-2 rounded-full p-0",
                            currentIndex === index
                                ? "bg-primary"
                                : "bg-primary/30"
                        )}
                        onClick={() => api?.scrollTo(index)}
                    />
                ))}
              </div>
          )}
        </div>

        {/* Dialog for showing original image */}
        <Dialog open={!!selectedImage} onOpenChange={(open) => !open && setSelectedImage(null)}>
          <DialogContent className="max-w-screen-lg w-full p-0 bg-transparent border-none">
            <div className="relative w-full max-h-[90vh] overflow-hidden flex items-center justify-center">
              {selectedImage && (
                  <img
                      src={`${import.meta.env.VITE_BASE_URL}${selectedImage}`}
                      alt="Original image"
                      className="max-w-full max-h-[90vh] object-contain"
                  />
              )}
            </div>
          </DialogContent>
        </Dialog>
      </>
  );
};