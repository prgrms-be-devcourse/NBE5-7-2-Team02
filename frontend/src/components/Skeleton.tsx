export function Skeleton() {
  return (
      <div className="flex flex-col items-center gap-4 max-w-3xl mx-auto mt-4">
        <div className="skeleton h-32 w-full"></div>
        <div className="skeleton h-4 w-1/2 self-start"></div>
        <div className="skeleton h-4 w-full"></div>
        <div className="skeleton h-4 w-full"></div>
      </div>
  );
}
