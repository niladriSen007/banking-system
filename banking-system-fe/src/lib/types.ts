export type ApiResponse<T> = {
    status: "success" | "error";
    statusCode: number;
    data: T | null;
    meta?: Record<string, unknown>,
    errors: ApiError[]
}

export type ApiError = {
    message?: string;
    code?: string
}