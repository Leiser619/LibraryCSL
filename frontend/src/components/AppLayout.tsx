import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

function cn(isActive: boolean) {
  return isActive
    ? "px-3 py-2 rounded-lg bg-black text-white"
    : "px-3 py-2 rounded-lg hover:bg-gray-100";
}

export default function AppLayout() {
  const { logout } = useAuth();
  const nav = useNavigate();

  return (
    <div className="min-h-screen">
      <header className="border-b">
        <div className="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
          <div className="font-bold">Library</div>

          <nav className="flex items-center gap-2">
            <NavLink to="/app/search" className={({ isActive }) => cn(isActive)}>
              Szukaj
            </NavLink>
            <div></div>
            <NavLink to="/app/library" className={({ isActive }) => cn(isActive)}>
              Moja biblioteka
            </NavLink>
            <div></div>
            <button
              className="ml-2 px-3 py-2 rounded-lg border hover:bg-gray-50"
              onClick={() => {
                logout();
                nav("/login", { replace: true });
              }}
            >
              Wyloguj
            </button>
          </nav>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-6 py-6">
        <Outlet />
      </main>
    </div>
  );
}