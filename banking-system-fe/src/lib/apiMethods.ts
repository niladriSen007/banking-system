import type { AxiosRequestConfig } from "axios";
import { api } from "./api";
import axios from "axios";
import type { ApiResponse } from "./types";

function getErrorMessage(error: unknown) {
  if (axios.isAxiosError(error)) {
    return (
      error.response?.data?.errors?.at(0)?.message ||
      error.message ||
      "Request failed"
    );
  }

  if (error instanceof Error) {
    return error?.message;
  }

  return "Something Went Wrong";
}

export async function GET<T>(url: string, config?: AxiosRequestConfig) {
  try {
    const response = await api.get<ApiResponse<T>>(url, config);

    if (
      response.data.status !== "success" ||
      response.data.statusCode !== 200
    ) {
      throw new Error(response.data.errors?.at(0)?.message || "Request failed");
    }

    return response.data.data;
  } catch (error: unknown) {
    throw new Error(getErrorMessage(error), { cause: error });
  }
}

export async function POST<TResponse, TRequest = unknown>(
  url: string,
  body: TRequest,
  config?: AxiosRequestConfig,
) {
  try {
    const response = await api.post<ApiResponse<TResponse>>(url, body, config);

    if (
      response.data.status !== "success" ||
      response.data.statusCode !== 200
    ) {
      throw new Error(response.data.errors?.at(0)?.message || "Request failed");
    }

    return response.data.data;
  } catch (error: unknown) {
    throw new Error(getErrorMessage(error), { cause: error });
  }
}

export async function PUT<TResponse, TRequest = unknown>(
  url: string,
  body?: TRequest,
  config?: AxiosRequestConfig,
) {
  try {
    const response = await api.put<ApiResponse<TResponse>>(url, body, config);

    if (
      response.data.status !== "success" ||
      response.data.statusCode !== 200
    ) {
      throw new Error(response.data.errors?.at(0)?.message || "Request failed");
    }

    return response.data.data;
  } catch (error) {
    throw new Error(getErrorMessage(error), { cause: error });
  }
}

export async function PATCH<TResponse, TRequest = unknown>(
  url: string,
  body?: TRequest,
  config?: AxiosRequestConfig,
) {
  try {
    const response = await api.patch<ApiResponse<TResponse>>(url, body, config);

    if (
      response.data.status !== "success" ||
      response.data.statusCode !== 200
    ) {
      throw new Error(response.data.errors?.at(0)?.message || "Request failed");
    }

    return response.data.data;
  } catch (error) {
    throw new Error(getErrorMessage(error), { cause: error });
  }
}

export async function DELETE<TResponse>(
  url: string,
  config?: AxiosRequestConfig,
) {
  try {
    const response = await api.delete<ApiResponse<TResponse>>(url, config);
    if (
      response.data.status !== "success" ||
      response.data.statusCode !== 200
    ) {
      throw new Error(response.data.errors?.at(0)?.message || "Request failed");
    }

    return response.data.data;
  } catch (error) {
    throw new Error(getErrorMessage(error), { cause: error });
  }
}
