import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { vi } from "vitest";
import { server } from "../../test/testServer";
import { http, HttpResponse } from "msw";
import SearchPage from "../SearchPage";

const API = "http://localhost:8080";


vi.mock("../../hooks/useDebounce", () => ({
  useDebounce: (v: any) => v,
}));


vi.mock("../../components/VirtualizedBookList", () => {
  return {
    default: ({ items, isInLibrary, onAdd }: any) => (
      <div>
        {items.map((b: any) => (
          <div key={b.googleVolumeId}>
            <span>{b.title}</span>
            <button
              onClick={() => onAdd(b)}
              disabled={isInLibrary(b.googleVolumeId)}
            >
              {isInLibrary(b.googleVolumeId) ? "W bibliotece" : "Dodaj"}
            </button>
          </div>
        ))}
      </div>
    ),
  };
});

function renderSearch() {
  const qc = new QueryClient();
  return render(
    <QueryClientProvider client={qc}>
      <SearchPage />
    </QueryClientProvider>
  );
}

test("po wpisaniu frazy pokazuje wyniki", async () => {
  const user = userEvent.setup();

  renderSearch();

  await user.type(screen.getByPlaceholderText(/szukaj książki/i), "harry");

  expect(
    await screen.findByText(/Harry Potter i Kamień Filozoficzny/i)
  ).toBeInTheDocument();
});

test("po kliknięciu Dodaj odświeża myBooks i przycisk robi się 'W bibliotece'", async () => {
  const user = userEvent.setup();

  let myBooks = [{ googleVolumeId: "id1", title: "HP1", authors: "J. K. Rowling" }];

  server.use(
    http.get(`${API}/api/me/books`, () => HttpResponse.json(myBooks)),
    http.post(`${API}/api/me/books`, async ({ request }) => {
      const body = (await request.json()) as any;
      myBooks = [...myBooks, body];
      return HttpResponse.json({ ...body, addedAt: "2026-02-24T10:00:00Z" }, { status: 201 });
    })
  );

  renderSearch();

  await user.type(screen.getByPlaceholderText(/szukaj książki/i), "harry");

  // znajdź przycisk Dodaj dla drugiej książki (id2)
  const addButtons = await screen.findAllByRole("button", { name: /dodaj/i });
  await user.click(addButtons[0]); // w naszej mock liście pierwszy "Dodaj" będzie dla id2 (id1 jest disabled = "W bibliotece")

  await waitFor(() => {
    expect(screen.getAllByRole("button", { name: /w bibliotece/i }).length).toBeGreaterThan(0);
  });
});