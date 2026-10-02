export type ApiResponse<T> = {
    status: "success" | "error";
    statusCode: number;
    data: T | null;
    message?: string | null;
    timestamp?: string;
    error?: string | null;
    meta?: Record<string, unknown>,
    errors?: ApiError[]
}

export type ApiError = {
    message?: string;
    code?: string
}

