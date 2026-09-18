// event-management APIへの共通fetchラッパー。RFC 9457 Problem Detailsをそのままエラーへ変換する。

export class ApiError extends Error {
  readonly status: number;
  readonly errors?: string[];

  constructor(status: number, detail: string, errors?: string[]) {
    super(detail);
    this.name = "ApiError";
    this.status = status;
    this.errors = errors;
  }
}

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

interface ProblemDetailBody {
  detail?: string;
  errors?: string[];
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      Accept: "application/json",
      ...init?.headers,
    },
  });

  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get("content-type") ?? "";
  const body: ProblemDetailBody | T | undefined = contentType.includes("json")
    ? await response.json()
    : undefined;

  if (!response.ok) {
    const problem = body as ProblemDetailBody | undefined;
    throw new ApiError(
      response.status,
      problem?.detail ?? `リクエストに失敗しました (status: ${response.status})`,
      problem?.errors,
    );
  }

  return body as T;
}

export const http = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, data: unknown) =>
    request<T>(path, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }),
  put: <T>(path: string, data: unknown) =>
    request<T>(path, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }),
  delete: (path: string) => request<void>(path, { method: "DELETE" }),
};
