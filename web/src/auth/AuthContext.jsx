import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { apiLogin, apiMe, apiRegister } from '../api.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let isMounted = true
    async function load() {
      if (!token) {
        setLoading(false)
        return
      }
      try {
        const me = await apiMe(token)
        if (isMounted) {
          setUser(me)
        }
      } catch {
        if (isMounted) {
          setToken(null)
          localStorage.removeItem('token')
        }
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }
    load()
    return () => {
      isMounted = false
    }
  }, [token])

  async function login(credentials) {
    const data = await apiLogin(credentials)
    setToken(data.token)
    localStorage.setItem('token', data.token)
    setUser(data.user)
  }

  async function register(payload) {
    const data = await apiRegister(payload)
    setToken(data.token)
    localStorage.setItem('token', data.token)
    setUser(data.user)
  }

  function logout() {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
  }

  const value = useMemo(
    () => ({
      token,
      user,
      loading,
      login,
      register,
      logout,
    }),
    [token, user, loading],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return ctx
}
