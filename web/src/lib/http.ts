type RequestConfig = {
  url: string;
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  params?: Record<string, unknown>;
  data?: unknown;
  headers?: Record<string, string>;
  signal?: AbortSignal;
  responseType?: 'json' | 'blob';
};

export class ApiError extends Error {
  readonly status: number;
  readonly code: string | undefined;
  constructor(status: number, code: string | undefined, message: string) {
    super(message);
    this.status = status;
    this.code = code;
  }
}

const baseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8000';

// El token de acceso vive solo en memoria; el refresh token va en cookie httpOnly.
let accessToken: string | null = null;
let onSessionLost: () => void = () => {};

export function setAccessToken(token: string | null) {
  accessToken = token;
}

export function setSessionLostHandler(handler: () => void) {
  onSessionLost = handler;
}

function toQuery(params?: Record<string, unknown>) {
  const search = new URLSearchParams();
  for (const [key, value] of Object.entries(params ?? {})) {
    if (value !== undefined && value !== null) search.set(key, String(value));
  }
  const text = search.toString();
  return text ? `?${text}` : '';
}

async function send(config: RequestConfig, options?: RequestInit) {
  const headers = new Headers(config.headers);
  if (config.data !== undefined) headers.set('Content-Type', 'application/json');
  if (accessToken) headers.set('Authorization', `Bearer ${accessToken}`);
  return fetch(`${baseUrl}${config.url}${toQuery(config.params)}`, {
    ...options,
    method: config.method,
    headers,
    body: config.data === undefined ? undefined : JSON.stringify(config.data),
    credentials: 'include',
    signal: config.signal,
  });
}

let refreshing: Promise<boolean> | null = null;

export function refreshSession(): Promise<boolean> {
  refreshing ??= fetch(`${baseUrl}/auth/refresh`, { method: 'POST', credentials: 'include' })
    .then(async (res) => {
      if (!res.ok) return false;
      accessToken = ((await res.json()) as { accessToken: string }).accessToken;
      return true;
    })
    .catch(() => false)
    .finally(() => {
      refreshing = null;
    });
  return refreshing;
}

export async function http<T>(config: RequestConfig, options?: RequestInit): Promise<T> {
  let res = await send(config, options);
  const isAuthCall = config.url.startsWith('/auth/');
  if (res.status === 401 && !isAuthCall) {
    if (await refreshSession()) {
      res = await send(config, options);
    } else {
      setAccessToken(null);
      onSessionLost();
    }
  }
  if (res.status === 204) return undefined as T;
  if (res.ok && config.responseType === 'blob') return (await res.blob()) as T;
  const body: unknown = await res.json().catch(() => undefined);
  if (!res.ok) {
    const problem = (body ?? {}) as { code?: string; detail?: string; title?: string };
    throw new ApiError(res.status, problem.code, problem.detail ?? problem.title ?? res.statusText);
  }
  return body as T;
}
