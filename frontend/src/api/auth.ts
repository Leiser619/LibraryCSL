import { api } from "./client";
import type { AuthResponse } from "../types/auth";

export async function registerApi(email: string, password: string) {
  await api.post("/api/auth/register", { email, password });
}

export async function loginApi(email: string, password: string) {
  const { data } = await api.post<AuthResponse>("/api/auth/login", { email, password });
  return data;
}