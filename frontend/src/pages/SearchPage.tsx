import { useMemo, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { searchBooks } from "../api/books";
import { addMyBook, getMyBooks } from "../api/meBooks";
import { useDebounce } from "../hooks/useDebounce";
import type { GoogleBook } from "../types/book";
import VirtualizedBookList from "../components/VirtualizedBookList";

const PAGE_SIZE = 10;

export default function SearchPage() {
  const [query, setQuery] = useState("");
  const [page, setPage] = useState(1);

  const debouncedQuery = useDebounce(query, 500);
  const queryClient = useQueryClient();

const myBooksQuery = useQuery({
  queryKey: ["myBooks"],
  queryFn: getMyBooks,
  staleTime: 1000 * 60 * 5,          // 5 min
  gcTime: 1000 * 60 * 60 * 24 * 7,   // 7dni w cache
});


  const myIds = useMemo(() => {
    return new Set((myBooksQuery.data ?? []).map((b) => b.googleVolumeId));
  }, [myBooksQuery.data]);

const searchQuery = useQuery({
  queryKey: ["search", debouncedQuery, page],
  queryFn: () => searchBooks(debouncedQuery, page, PAGE_SIZE),
  enabled: debouncedQuery.trim().length > 0,
  staleTime: 1000 * 60 * 2,          // 2 min świeże
  gcTime: 1000 * 60 * 60 * 24,       // 1 dzień w cache
});
  const addMutation = useMutation({
    mutationFn: addMyBook,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["myBooks"] });
    },
  });

  function handleAdd(book: GoogleBook) {
    addMutation.mutate({
      googleVolumeId: book.googleVolumeId,
      title: book.title,
      authors: book.authors,
      thumbnail: book.thumbnail,
    });
  }

  return (
    <div>
      <h1 className="text-2xl font-bold">Wyszukiwanie książek</h1>

      <input
        className="mt-6 w-full rounded-lg border px-4 py-2"
        placeholder="Szukaj książki..."
        value={query}
        onChange={(e) => {
          setQuery(e.target.value);
          setPage(1);
        }}
      />

      {searchQuery.isLoading && <p className="mt-4">Ładowanie...</p>}
      {searchQuery.isError && (
        <p className="mt-4 text-red-600">Błąd pobierania wyników.</p>
      )}

      {debouncedQuery.trim().length > 0 &&
        !searchQuery.isLoading &&
        (searchQuery.data?.items?.length ?? 0) === 0 && (
          <p className="mt-4 text-gray-600">Brak wyników.</p>
        )}

        {searchQuery.data?.items && searchQuery.data.items.length > 0 && (
          <VirtualizedBookList
            items={searchQuery.data.items}
            isInLibrary={(id) => myIds.has(id)}
            isAdding={addMutation.isPending}
            onAdd={handleAdd}
          />
        )}

    {searchQuery.data && (
      <div className="flex justify-between items-center mt-6">
        <button
          className="border px-4 py-2 rounded disabled:opacity-50"
          disabled={page === 1}
          onClick={() => setPage((p) => p - 1)}
        >
          Poprzednia
        </button>

        <div className="text-sm text-gray-600">
          Strona {page} • {Math.min(page * PAGE_SIZE, searchQuery.data.totalItems)} /{" "}
          {searchQuery.data.totalItems}
        </div>

        <button
          className="border px-4 py-2 rounded disabled:opacity-50"
          disabled={page * PAGE_SIZE >= searchQuery.data.totalItems}
          onClick={() => setPage((p) => p + 1)}
        >
          Następna
        </button>
      </div>
    )}
    </div>
  );
}

