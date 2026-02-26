import { api } from "./client";
import type { ReadBookCreateRequest, ReadBookResponse } from "../types/book";

export async function getMyBooks() {
  const { data } = await api.get<ReadBookResponse[]>("/me/books");
  return data;
}

export async function addMyBook(req: ReadBookCreateRequest) {
  const { data } = await api.post<ReadBookResponse>("/me/books", req);
  return data;
}

export async function removeMyBook(googleVolumeId: string) {
  await api.delete(`/me/books/${encodeURIComponent(googleVolumeId)}`);
}