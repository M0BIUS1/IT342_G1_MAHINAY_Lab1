const BASE_URL = 'http://localhost:8080'

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  })

  if (!res.ok) {
    let message = 'Request failed'
    try {
      const data = await res.json()
      if (data?.message) message = data.message
    } catch {
      // ignore
    }
    throw new Error(message)
  }

  if (res.status === 204) return null
  return res.json()
}

export function apiRegister(payload) {
  return request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function apiLogin(payload) {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function apiMe(token) {
  return request('/api/user/me', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
}
