import { create } from "zustand";

type AuthStatus = "idle" | "loading" | "ready" | "error";

type AuthStore = {
  status: AuthStatus;
  user: null;
  error: string | null;
  isAuthenticated: boolean;

  setLoading: () => void;
  setUser: (user: null) => void;
  setError: (errorMessage: string) => void;
  logout: () => void;
};

export const useAuthStore = create<AuthStore>((set) => ({
  status: "idle",
  user: null,
  error: null,
  isAuthenticated: false,

  setLoading: () =>
    set({
      status: "loading",
      error: null,
    }),
  setUser: (user) =>
    set({
      status: "ready",
      error: null,
      user,
      isAuthenticated: true,
    }),
  setError: (error) =>
    set({
      status: "error",
      user: null,
      isAuthenticated: false,
      error,
    }),
  logout: () =>
    set({
      status: "idle",
      error: null,
      isAuthenticated: false,
      user: null,
    }),
}));
