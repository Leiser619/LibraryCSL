import { api } from "./client";
import type { ReadBookCreateRequest, ReadBookResponse } from "../types/book";

export async function getMyBooks() {
  const { data } = await api.get<ReadBookResponse[]>("/api/me/books");
  return data;
}

export async function addMyBook(req: ReadBookCreateRequest) {
  const { data } = await api.post<ReadBookResponse>("/api/me/books", req);
  return data;
}

export async function removeMyBook(googleVolumeId: string) {
  await api.delete(`/api/me/books/${encodeURIComponent(googleVolumeId)}`);
}