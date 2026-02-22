import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getMyBooks, removeMyBook } from "../api/meBooks";
import { Link } from "react-router-dom";

export default function LibraryPage() {
  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ["myBooks"],
    queryFn: getMyBooks,
  });

  const deleteMutation = useMutation({
    mutationFn: removeMyBook,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["myBooks"] });
    },
  });

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Moje przeczytane książki</h1>
        <Link to="/app/search" className="underline">
          Wróć do wyszukiwania
        </Link>
      </div>

      {isLoading && <p className="mt-4">Ładowanie...</p>}

      <div className="mt-6 space-y-4">
        {data?.map((book) => (
          <div key={book.googleVolumeId} className="border rounded-xl p-4 flex justify-between">
            <div>
              <h2 className="font-semibold">{book.title}</h2>
                <p className="text-sm text-gray-600">{book.authors ?? ""}</p>
            </div>

            <button
              onClick={() => deleteMutation.mutate(book.googleVolumeId)}
              className="text-red-600"
            >
              Usuń
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}