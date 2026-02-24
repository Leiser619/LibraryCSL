import { http, HttpResponse } from "msw";

const API = "http://localhost:8080";

export const handlers = [
  http.post(`${API}/api/auth/login`, async () => {
    return HttpResponse.json({ accessToken: "test.jwt.token" });
  }),

  http.post(`${API}/api/auth/register`, async () => {
    return new HttpResponse(null, { status: 201 });
  }),

  http.get(`${API}/api/books/search`, ({ request }) => {
    const url = new URL(request.url);
    const q = url.searchParams.get("q") ?? "";
    const page = Number(url.searchParams.get("page") ?? "1");
    const size = Number(url.searchParams.get("size") ?? "10");

    return HttpResponse.json({
      page,
      size,
      totalItems: 2,
      items: q
        ? [
            {
              googleVolumeId: "id1",
              title: "Harry Potter i Kamień Filozoficzny",
              authors: "J. K. Rowling",
              thumbnail: "",
            },
            {
              googleVolumeId: "id2",
              title: "Harry Potter i Komnata Tajemnic",
              authors: "J. K. Rowling",
              thumbnail: "",
            },
          ]
        : [],
    });
  }),

  http.get(`${API}/api/me/books`, () => {
    return HttpResponse.json([
      { googleVolumeId: "id1", title: "HP1", authors: "J. K. Rowling" },
    ]);
  }),

  http.post(`${API}/api/me/books`, async ({ request }) => {
    const body = (await request.json()) as any;
    return HttpResponse.json({ ...body, addedAt: "2026-02-24T10:00:00Z" }, { status: 201 });
  }),

  http.delete(`${API}/api/me/books/:googleVolumeId`, () => {
    return new HttpResponse(null, { status: 204 });
  }),
];