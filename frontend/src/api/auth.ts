import { api } from "./client";
import type { AuthResponse } from "../types/auth";

export async function registerApi(email: string, password: string): Promise<void> {
  await api.post("/auth/register", { email, password });
}

export async function loginApi(email: string, password: string): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>("/auth/login", { email, password });
  return data;
}