import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

/**
 * Navbar Component
 *
 * Features:
 * - Display current user info
 * - Logout button
 * - Role-based styling
 */

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (!user) {
    return null;
  }

  const roleColor = user.role === "ADMIN" ? "bg-purple-600" : "bg-blue-600";

  return (
    <nav className="bg-white shadow-md mb-6">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Left: App Title */}
          <div>
            <h1 className="text-xl font-bold text-gray-800">Auction System</h1>
          </div>

          {/* Right: User Info & Logout */}
          <div className="flex items-center gap-4">
            {/* User Badge */}
            <div className="flex items-center gap-2">
              <div
                className={`${roleColor} text-white px-3 py-1 rounded-full text-sm font-medium`}
              >
                {user.role}
              </div>
              <span className="text-gray-700 font-medium">{user.username}</span>
              {user.team && (
                <span className="text-gray-500 text-sm">
                  ({user.team.name})
                </span>
              )}
            </div>

            {/* Logout Button */}
            <button
              onClick={handleLogout}
              className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition duration-200"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
}
