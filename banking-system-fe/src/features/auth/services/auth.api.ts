import { GET, POST } from "@/lib/apiMethods";
import type {
  AuthUserResponse,
  LoginRequest,
  LoginResponse,
  RefreshTokenResponse,
  SignupRequest,
  SignupResponse,
} from "../types";

export const signupMutation = async (signupData: SignupRequest) => {
  return await POST<SignupResponse, SignupRequest>("/auth/signup", signupData);
};

export const loginMutation = async (
  loginData: LoginRequest,
): Promise<LoginResponse> => {
  const loginResponse = await POST<LoginResponse, LoginRequest>(
    "/auth/login",
    loginData,
  );

  if (!loginResponse?.userResponse || !loginResponse.accessToken) {
    throw new Error("Login response did not include a user and access token.");
  }

  return loginResponse;
};

export const refreshTokenMutation = async (): Promise<RefreshTokenResponse> => {
  const response = await POST<RefreshTokenResponse, Record<string, never>>(
    "/auth/refresh-token",
    {},
  );

  if (!response?.accessToken) {
    throw new Error("Refresh response did not include an access token.");
  }

  return response;
};

export const currentUserQuery = async (): Promise<AuthUserResponse> => {
  const user = await GET<AuthUserResponse>("/auth/users/profile");

  if (!user) {
    throw new Error("Profile response did not include a user.");
  }

  return user;
};
