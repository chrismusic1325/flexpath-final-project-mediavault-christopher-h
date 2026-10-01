function extractToken(value) {
  if (!value) {
    return "";
  }

  if (typeof value === "string") {
    const candidate =
      value.replace(/^"|"$/g, "").trim();

    return candidate.split(".").length === 3
      ? candidate
      : "";
  }

  if (typeof value === "object") {
    const preferredKeys = [
      "token",
      "accessToken",
      "jwt",
      "authToken",
    ];

    for (const key of preferredKeys) {
      if (key in value) {
        const token =
          extractToken(value[key]);

        if (token) {
          return token;
        }
      }
    }

    for (const child of Object.values(value)) {
      const token =
        extractToken(child);

      if (token) {
        return token;
      }
    }
  }

  return "";
}

async function parseResponse(response) {
  const text = await response.text();

  let data = text;

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!response.ok) {
    let message =
      `Request failed (${response.status})`;

    if (typeof data === "string" &&
        data.trim()) {
      message = data;
    } else if (data?.message) {
      message = data.message;
    } else if (data?.error) {
      message = data.error;
    }

    throw new Error(message);
  }

  return data;
}

export function getToken() {
  return localStorage.getItem(
    "mediavault_token"
  ) || "";
}

export function getUsername() {
  return localStorage.getItem(
    "mediavault_username"
  ) || "";
}

export function logout() {
  localStorage.removeItem(
    "mediavault_token"
  );

  localStorage.removeItem(
    "mediavault_username"
  );
}

export async function login(
  username,
  password
) {
  const response = await fetch(
    "/auth/login",
    {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type":
          "application/json",
      },
      body: JSON.stringify({
        username,
        password,
      }),
    }
  );

  const data =
    await parseResponse(response);

  const token =
    extractToken(data);

  if (token) {
    localStorage.setItem(
      "mediavault_token",
      token
    );
  }

  localStorage.setItem(
    "mediavault_username",
    username
  );

  return data;
}

export async function api(
  path,
  options = {}
) {
  const token = getToken();

  const headers = {
    ...(options.body
      ? {
          "Content-Type":
            "application/json",
        }
      : {}),
    ...(options.headers || {}),
  };

  if (token) {
    headers.Authorization =
      `Bearer ${token}`;
  }

  const response = await fetch(
    path,
    {
      ...options,
      headers,
      credentials: "include",
    }
  );

  return parseResponse(response);
}

export async function register(
  username,
  password
) {
  return api(
    "/api/users",
    {
      method: "POST",
      body: JSON.stringify({
        username,
        password,
      }),
    }
  );
}

export { extractToken };
