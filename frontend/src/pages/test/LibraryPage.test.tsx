import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider, createMemoryRouter } from "react-router-dom";
import { server } from "../../test/testServer";
import { http, HttpResponse } from "msw";
import LibraryPage from "../LibraryPage";

const API = "http://localhost:8080";

function renderLibrary() {
  const qc = new QueryClient();
  const router = createMemoryRouter(
    [{ path: "/app/library", element: <LibraryPage /> }],
    { initialEntries: ["/app/library"] }
  );

  return render(
    <QueryClientProvider client={qc}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  );
}

test("wyświetla książki i pozwala usunąć", async () => {
  const user = userEvent.setup();

  let books = [{ googleVolumeId: "id1", title: "HP1", authors: "J. K. Rowling" }];

  server.use(
    http.get(`${API}/api/me/books`, () => HttpResponse.json(books)),
    http.delete(`${API}/api/me/books/:googleVolumeId`, ({ params }) => {
      books = books.filter((b) => b.googleVolumeId !== params.googleVolumeId);
      return new HttpResponse(null, { status: 204 });
    })
  );

  renderLibrary();

  expect(screen.getByText(/ładowanie/i)).toBeInTheDocument();

  expect(await screen.findByText("HP1")).toBeInTheDocument();

  await user.click(screen.getByRole("button", { name: /usuń/i }));

  await waitFor(() => {
    expect(screen.queryByText("HP1")).not.toBeInTheDocument();
  });
});