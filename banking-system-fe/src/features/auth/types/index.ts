import { formOptions } from "@tanstack/react-form";
import { z } from "zod";

export interface LoginRequest {
  email: string;
  password: string;
}
const defaultLoginData: LoginRequest = { email: "", password: "" };

const loginSchema = z.object({
  email: z.string().min(6, "Email must be 6 characters long"),
  password: z.string().min(1, "Password must be 1 character long"),
});

export const loginFormOpts = formOptions({
  defaultValues: defaultLoginData,
  validators: {
    onChange: loginSchema,
    onSubmit: loginSchema,
  },
});

export interface SignupRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  password: string;
}

export interface AuthUserResponse {
  createdAt: string;
  email: string;
  firstName: string;
  id: number;
  lastLoggedInTime: string | null;
  lastName: string;
  phoneNumber: string;
  profileImage: string | null;
  role: string;
  status: string;
  userPermissions: string[];
}

export interface SignupResponse {
  message: string;
  title: string;
  userResponse: AuthUserResponse;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  message: string;
  title: string;
  userResponse: AuthUserResponse;
}

export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
  message: string;
  title: string;
}

const defaultSignupData: SignupRequest = {
  firstName: "",
  lastName: "",
  email: "",
  phoneNumber: "",
  password: "",
};

const signupSchema = z.object({
  firstName: z.string().min(1, "First name must be 1 characters long"),
  lastName: z.string().min(1, "Last name must be 6 characters long"),
  email: z.string().min(3, "Email must be 6 characters long"),
  phoneNumber: z.string().min(1),
  password: z.string().min(1, "Password must be 1 character long"),
});

export const signupFormOpts = formOptions({
  defaultValues: defaultSignupData,
  validators: {
    onChange: signupSchema,
  },
});
