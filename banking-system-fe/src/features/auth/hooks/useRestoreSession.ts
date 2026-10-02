import { useEffect } from "react";
import { useAuthStore } from "../store/auth.store";
import type { UserRole, UserState } from "../store/auth.store";
import type { AuthUserResponse } from "../types";
import { currentUserQuery, refreshTokenMutation } from "../services/auth.api";

let sessionRestorePromise: Promise<void> | null = null;

function toUserState(user: AuthUserResponse): UserState {
  const normalizedRole = user.role.toLowerCase();
  const role: UserRole[] =
    normalizedRole === "admin" || normalizedRole === "customer"
      ? [normalizedRole]
      : [];

    // console.log(user)

  return {
    id: String(user.id),
    name: `${user.firstName} ${user.lastName}`.trim(),
    email: user.email,
    phoneNumber: user.phoneNumber,
    role,
  };
}

export const useRestoreSession = () => {
  useEffect(() => {
    if (sessionRestorePromise) {
      return;
    }

    sessionRestorePromise = (async () => {
      try {
        const { accessToken } = await refreshTokenMutation();
        useAuthStore.getState().setAccessToken(accessToken);

        const user = await currentUserQuery();
        useAuthStore.getState().setUser(toUserState(user));
      } catch {
        useAuthStore.getState().logout();
      } finally {
        sessionRestorePromise = null;
      }
    })();
  }, []);
};