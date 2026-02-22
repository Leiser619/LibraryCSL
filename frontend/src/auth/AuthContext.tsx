import React, { createContext, useContext, useMemo, useState } from "react";
import { tokenStorage } from "./tokenStorage";
import { loginApi } from "../api/auth";

type AuthContextValue = {
  token: string | null;
  isAuthed: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(tokenStorage.get());

  const value = useMemo<AuthContextValue>(() => {
    return {
      token,
      isAuthed: !!token,
      async login(email, password) {
        const res = await loginApi(email, password);
       tokenStorage.set(res.accessToken);
       setToken(res.accessToken);

      },
      logout() {
        tokenStorage.clear();
        setToken(null);
      },
    };
  }, [token]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}