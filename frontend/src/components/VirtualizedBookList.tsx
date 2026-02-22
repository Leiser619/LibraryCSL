import { FixedSizeList as List, ListChildComponentProps } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import type { GoogleBook } from "../types/book";

type Props = {
  items: GoogleBook[];
  isInLibrary: (id: string) => boolean;
  isAdding: boolean;
  onAdd: (b: GoogleBook) => void;
};

const ROW_HEIGHT = 140;

export default function VirtualizedBookList({ items, isInLibrary, isAdding, onAdd }: Props) {
  const itemData = { items, isInLibrary, isAdding, onAdd };

  return (
    <div className="mt-6 h-[70vh] border rounded-xl overflow-hidden">
      <AutoSizer>
        {({ height, width }) => (
          <List
            height={height}
            width={width}
            itemCount={items.length}
            itemSize={ROW_HEIGHT}
            itemData={itemData}
          >
            {Row}
          </List>
        )}
      </AutoSizer>
    </div>
  );
}

function Row({ index, style, data }: ListChildComponentProps) {
  const book: GoogleBook = data.items[index];
  const inLib = data.isInLibrary(book.googleVolumeId);

  return (
    <div style={style} className="border-b last:border-b-0">
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
            onClick={() => data.onAdd(book)}
            disabled={inLib || data.isAdding}
            className="mt-2 px-3 py-1 rounded text-white bg-black disabled:opacity-50"
          >
            {inLib ? "Już w przeczytanych" : "Dodaj do przeczytanych"}
          </button>
        </div>
      </div>
    </div>
  );
}