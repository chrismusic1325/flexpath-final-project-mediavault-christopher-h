import {
  Link,
  useNavigate,
} from "react-router-dom";

import {
  getUsername,
  logout,
} from "../api";

export default function NavBar() {
  const navigate = useNavigate();
  const username = getUsername();

  function signOut() {
    logout();
    navigate("/login");
    window.location.reload();
  }

  return (
    <header className="topbar">
      <Link
        className="brand"
        to="/"
      >
        MediaVault
      </Link>

      <nav>
        <Link to="/">
          Dashboard
        </Link>

        <Link to="/items">
          Items
        </Link>

        <Link to="/collections">
          Collections
        </Link>

        <Link to="/public">
          Public Library
        </Link>

        <Link to="/admin">
          Admin
        </Link>

        {!username ? (
          <Link to="/login">
            Login
          </Link>
        ) : (
          <button
            className="link-button"
            onClick={signOut}
          >
            Logout ({username})
          </button>
        )}
      </nav>
    </header>
  );
}
