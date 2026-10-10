const API_BASE = (
  import.meta.env.VITE_API_URL ||
  "http://localhost:8080"
).replace(/\/+$/, "");

const API = /\/api$/i.test(API_BASE)
  ? API_BASE
  : `${API_BASE}/api`;

// Read the saved JWT token
export function token() {
  return localStorage.getItem("dfps_token");
}

// Common API request function
export async function request(path, options = {}) {
  const headers = new Headers(options.headers || {});

  const jwt = token();

  if (jwt) {
    headers.set("Authorization", `Bearer ${jwt}`);
  }

  if (
    options.body &&
    !(options.body instanceof FormData)
  ) {
    headers.set("Content-Type", "application/json");
  }

  // Normalize the path so the API URL is not duplicated
  const normalizedPath = path.startsWith("/")
    ? path
    : `/${path}`;

  const res = await fetch(`${API}${normalizedPath}`, {
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
      data?.message ||
      data?.error ||
      (typeof data === "string" && data) ||
      `Request failed (${res.status})`
    );
  }

  return data;
}

// Helper for JSON requests
export const json = (method, body) => ({
  method,
  body: JSON.stringify(body),
});
