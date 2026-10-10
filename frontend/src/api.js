
const API = (
  import.meta.env.VITE_API_URL ||
  "http://localhost:8080"
).replace(/\/+$/, "");

export function token() {
  return localStorage.getItem("dfps_token");
}

export async function request(path, options = {}) {
  const headers = new Headers(options.headers || {});

  if (token()) {
    headers.set("Authorization", `Bearer ${token()}`);
  }

  if (options.body && !(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  const res = await fetch(`${API}${path}`, {
    ...options,
    headers,
  });

  const text = await res.text();
  let data = null;

  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  if (!res.ok) {
    throw new Error(
      data?.message || data?.error || `Request failed (${res.status})`
    );
  }

  return data;
}

export const json = (method, body) => ({
  method,
  body: JSON.stringify(body),
});