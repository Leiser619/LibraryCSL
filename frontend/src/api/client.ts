import axios from "axios";
import { tokenStorage } from "../auth/tokenStorage";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
});

api.interceptors.request.use((config) => {
  const url = config.url ?? "";

  const isAuthEndpoint =
    url.startsWith("/auth/") ||
    url === "/auth/login" ||
    url === "/auth/register";

  if (!isAuthEndpoint) {
    const token = tokenStorage.get();
    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }
  } else {
    if (config.headers) {
      delete (config.headers as any).Authorization;
    }
  }

  return config;
});