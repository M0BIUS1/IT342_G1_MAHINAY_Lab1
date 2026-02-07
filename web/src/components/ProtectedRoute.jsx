import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'

function ProtectedRoute({ children }) {
  const { token, loading } = useAuth()

  if (loading) {
    return (
      <div className="page">
        <div className="card">Loading...</div>
      </div>
    )
  }

  if (!token) {
    return <Navigate to="/login" replace />
  }

  return children
}

export default ProtectedRoute
