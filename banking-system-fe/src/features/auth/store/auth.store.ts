import { create } from "zustand";

type AuthStatus = "idle" | "loading" | "ready" | "error";

export type UserRole = "admin" | "customer";

export type UserState = {
  id: string;
  name: string;
  email: string;
  role: UserRole[];
};

type AuthStore = {
  status: AuthStatus;
  user: UserState | null;
  accessToken: string | null;
  error: string | null;
  isAuthenticated: boolean;

  setIsAuthenticated: (authenticated: boolean) => void;
  setAccessToken: (accessToken: string | null) => void;
  setLoading: () => void;
  setUser: (user: UserState | null) => void;
  setError: (errorMessage: string) => void;
  logout: () => void;
};

export const useAuthStore = create<AuthStore>((set) => ({
  status: "loading",
  user: null,
  accessToken: null,
  error: null,
  isAuthenticated: false,

  setIsAuthenticated: (authenticated: boolean) =>
    set({
      isAuthenticated: authenticated,
    }),
  setAccessToken: (accessToken: string | null) => set({ accessToken }),
  setLoading: () =>
    set({
      status: "loading",
      error: null,
    }),
  setUser: (user: UserState | null) =>
    set({
      status: "ready",
      error: null,
      user,
      isAuthenticated: user !== null,
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
      accessToken: null,
    }),
}));
