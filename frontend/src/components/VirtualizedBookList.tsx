import { useRef } from "react";
import { useVirtualizer } from "@tanstack/react-virtual";
import type { GoogleBook } from "../types/book";

type Props = {
  items: GoogleBook[];
  isInLibrary: (id: string) => boolean;
  isAdding: boolean;
  onAdd: (b: GoogleBook) => void;
};

const ROW_HEIGHT = 140;

export default function VirtualizedBookList({ items, isInLibrary, isAdding, onAdd }: Props) {
  const parentRef = useRef<HTMLDivElement | null>(null);

  const rowVirtualizer = useVirtualizer({
    count: items.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => ROW_HEIGHT,
    overscan: 8,
  });

  const virtualItems = rowVirtualizer.getVirtualItems();

  return (
    <div className="mt-6 border rounded-xl overflow-hidden">
      {/* to jest scroll container */}
      <div ref={parentRef} className="h-[70vh] w-full overflow-auto">
        {/* to jest “spacer” o pełnej wysokości */}
        <div style={{ height: rowVirtualizer.getTotalSize(), position: "relative" }}>
          {virtualItems.map((v) => {
            const book = items[v.index];
            const inLib = isInLibrary(book.googleVolumeId);

            return (
              <div
                key={v.key}
                style={{
                  position: "absolute",
                  top: 0,
                  left: 0,
                  width: "100%",
                  transform: `translateY(${v.start}px)`,
                  height: v.size,
                }}
                className="border-b last:border-b-0 bg-white"
              >
                <div className="p-4 flex gap-4 h-full">
                  {book.thumbnail ? (
                    <img
                      src={book.thumbnail}
                      alt={book.title}
                      className="w-20 h-28 object-cover rounded flex-none"
                    />
                  ) : (
                    <div className="w-20 h-28 rounded bg-gray-100 flex-none" />
                  )}

                  <div className="flex-1 min-w-0">
                    <div className="font-semibold truncate">{book.title}</div>
                    <div className="text-sm text-gray-600 truncate">{book.authors ?? ""}</div>

                    <button
                      onClick={() => onAdd(book)}
                      disabled={inLib || isAdding}
                      className="mt-2 px-3 py-1 rounded text-white bg-black disabled:opacity-50"
                    >
                      {inLib ? "Już w przeczytanych" : "Dodaj do przeczytanych"}
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}