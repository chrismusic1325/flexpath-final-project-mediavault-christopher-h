import {
  useEffect,
  useState,
} from "react";

import {
  api,
} from "../api";

export default function AdminPage() {
  const [
    users,
    setUsers,
  ] = useState([]);

  const [
    message,
    setMessage,
  ] = useState("");

  useEffect(() => {
    api("/api/users")
      .then((data) => {
        setUsers(
          Array.isArray(data)
            ? data
            : []
        );
      })
      .catch((error) => {
        setMessage(
          `${error.message} — this page requires an ADMIN account.`
        );
      });
  }, []);

  return (
    <section>
      <h1>
        Administration
      </h1>

      {message && (
        <p className="message">
          {message}
        </p>
      )}

      <div className="card-grid">
        {users.map((user) => (
          <article
            className="card"
            key={
              user.username
            }
          >
            <h2>
              {user.username}
            </h2>

            <p>
              Roles:
              {" "}
              {
                (
                  user.roles || []
                ).join(", ") ||
                "None"
              }
            </p>
          </article>
        ))}
      </div>
    </section>
  );
}
