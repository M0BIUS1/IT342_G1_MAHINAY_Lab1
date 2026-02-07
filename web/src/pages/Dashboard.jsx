import { useAuth } from '../auth/AuthContext.jsx'

function DashboardPage() {
  const { user, logout } = useAuth()

  return (
    <div className="page">
      <div className="card">
        <div className="header">
          <div>
            <h1>Dashboard</h1>
            <p className="subtitle">Your profile details</p>
          </div>
          <button className="ghost" onClick={logout}>
            Logout
          </button>
        </div>

        <div className="profile">
          <div>
            <span className="label">Name</span>
            <div className="value">{user?.name}</div>
          </div>
          <div>
            <span className="label">Email</span>
            <div className="value">{user?.email}</div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default DashboardPage
