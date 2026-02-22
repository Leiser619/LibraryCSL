import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";
import type { ReactElement } from "react";

type Props = {
  children: ReactElement;
};

export default function ProtectedRoute({ children }: Props) {
  const { isAuthed } = useAuth();

  if (!isAuthed) {
    return <Navigate to="/login" replace />;
  }

  return children;
}