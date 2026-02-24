import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { RouterProvider, createMemoryRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import LoginPage from "../LoginPage";
import { AuthProvider } from "../../auth/AuthContext";

function renderLogin() {
  const router = createMemoryRouter(
    [{ path: "/login", element: <LoginPage /> }],
    { initialEntries: ["/login"] }
  );

  const qc = new QueryClient();

  return render(
    <QueryClientProvider client={qc}>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </QueryClientProvider>
  );
}

test("po zalogowaniu zapisuje token do localStorage", async () => {
  const user = userEvent.setup();
  renderLogin();

  await user.type(screen.getByRole("textbox", { name: "" }), "a@a.com");
  await user.type(screen.getByDisplayValue(""), "abcdef");

  await user.click(screen.getByRole("button", { name: /zaloguj/i }));

  await waitFor(() => {
    expect(localStorage.getItem("access_token")).toBe("test.jwt.token");
  });
});