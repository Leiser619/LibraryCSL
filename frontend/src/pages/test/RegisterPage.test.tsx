import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider, createMemoryRouter } from "react-router-dom";
import RegisterPage from "../RegisterPage";

function renderRegister() {
  const qc = new QueryClient();
  const router = createMemoryRouter(
    [
      { path: "/register", element: <RegisterPage /> },
      { path: "/login", element: <div>LOGIN_PAGE</div> },
    ],
    { initialEntries: ["/register"] }
  );

  return render(
    <QueryClientProvider client={qc}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  );
}

test("po rejestracji przechodzi na /login", async () => {
  const user = userEvent.setup();
  const { container } = renderRegister();

  const email = container.querySelector('input[name="email"]')!;
  const password = container.querySelector('input[name="password"]')!;

  await user.type(email, "a@a.com");
  await user.type(password, "abcdef");
  await user.click(screen.getByRole("button", { name: /utwórz konto/i }));

  await waitFor(() => {
    expect(screen.getByText("LOGIN_PAGE")).toBeInTheDocument();
  });
});