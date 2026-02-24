import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import { RouterProvider } from "react-router-dom";
import { QueryClientProvider } from "@tanstack/react-query";
import { router } from "./router";
import { AuthProvider } from "./auth/AuthContext";
import { queryClient } from "./queryClient";
import { registerSW } from "virtual:pwa-register";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </QueryClientProvider>
  </React.StrictMode>
);

// W dev najlepiej wyłączyć SW, żeby nie robił cyrków z cache.
// Włączamy tylko na build/preview (production).
if (import.meta.env.PROD) {
  registerSW({ immediate: true });
}