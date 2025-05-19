import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";
import { cn } from "@/lib/utils";

interface ImageCarouselProps {
  images: string[];
  className?: string;
}

export function ImageComponent({ images, className }: ImageCarouselProps) {
  return (
      <Carousel
          className={cn("w-full max-w-md mx-auto", className)}
          opts={{
            align: "center",
            loop: true,
          }}
      >
        <CarouselContent className="h-64">
          {images.map((image, index) => (
              <CarouselItem key={index} className="h-full">
                <div className="h-full w-full p-1">
                  <div className="relative h-full w-full overflow-hidden rounded-md">
                    <img
                        src={image}
                        alt={`Slide ${index + 1}`}
                        className="h-full w-full object-cover"
                        loading={index === 0 ? "eager" : "lazy"}
                    />
                  </div>
                </div>
              </CarouselItem>
          ))}
        </CarouselContent>

        {images.length > 1 && (
            <>
              <CarouselPrevious
                  className="absolute left-1 bg-background/80 hover:bg-background border-border text-foreground"
              />
              <CarouselNext
                  className="absolute right-1 bg-background/80 hover:bg-background border-border text-foreground"
              />
            </>
        )}
      </Carousel>
  );
}