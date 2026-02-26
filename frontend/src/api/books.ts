import { api } from "./client";
import type { BookSearchResponse } from "../types/book";

export async function searchBooks(q: string, page: number, size: number) {
  const { data } = await api.get<BookSearchResponse>("/books/search", {
    params: { q, page, size },
  });
  return data;
}