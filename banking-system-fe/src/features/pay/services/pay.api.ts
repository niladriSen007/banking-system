import axios from "axios";
import { GET, POST } from "@/lib/apiMethods";
import type {
  CreatePayAccountRequest,
  CreatePayAccountResponse,
} from "../types";

export const getPayAccountByEmailQuery = async (email: string) => {
  try {
    return await GET<CreatePayAccountResponse | null>("/accounts/by-email", {
      params: { email },
    });
  } catch (error) {
    if (
      error instanceof Error &&
      axios.isAxiosError(error.cause) &&
      error.cause.response?.status === 404
    ) {
      return null;
    }

    throw error;
  }
};

export const createPayAccountMutation = (request: CreatePayAccountRequest) =>
  POST<CreatePayAccountResponse, CreatePayAccountRequest>("/accounts", request);