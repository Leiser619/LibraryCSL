import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { registerApi } from "../api/auth";

const schema = z.object({
  email: z.string().email("Nieprawidłowy email"),
  password: z.string().min(6, "Minimum 6 znaków"),
});
type FormData = z.infer<typeof schema>;

export default function RegisterPage() {
  const nav = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    setError,
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const onSubmit = async (data: FormData) => {
    try {
      await registerApi(data.email, data.password);
      nav("/login");
    } catch (e: any) {
      setError("email", { message: "Nie udało się zarejestrować (email zajęty?)" });
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6">
      <div className="w-full max-w-md rounded-2xl border p-6 shadow">
        <h1 className="text-2xl font-bold">Rejestracja</h1>
        <p className="mt-2 text-sm text-gray-600">
          Masz konto? <Link className="underline" to="/login">Zaloguj się</Link>
        </p>

        <form className="mt-6 space-y-4" onSubmit={handleSubmit(onSubmit)}>
          <div>
            <label className="text-sm font-medium">Email</label>
            <input className="mt-1 w-full rounded-lg border px-3 py-2" {...register("email")} />
            {errors.email && <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>}
          </div>

          <div>
            <label className="text-sm font-medium">Hasło</label>
            <input
              className="mt-1 w-full rounded-lg border px-3 py-2"
              type="password"
              {...register("password")}
            />
            {errors.password && (
              <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
            )}
          </div>

          <button
            className="w-full rounded-lg bg-black text-white py-2 disabled:opacity-60"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Tworzenie..." : "Utwórz konto"}
          </button>
        </form>
      </div>
    </div>
  );
}