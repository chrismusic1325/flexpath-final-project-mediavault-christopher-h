import React, {
  useCallback,
  useEffect,
  useState,
} from "react";

import {
  api,
} from "../api";

import {
  buildQuery,
  visibilityLabel,
} from "../utils";

export default function CollectionsPage() {
  const [
    collections,
    setCollections,
  ] = useState([]);

  const [
    name,
    setName,
  ] = useState("");

  const [
    owner,
    setOwner,
  ] = useState("");

  const [
    sortBy,
    setSortBy,
  ] = useState(
    "createdDate"
  );

  const [
    direction,
    setDirection,
  ] = useState("desc");

  const [
    message,
    setMessage,
  ] = useState("");

  const [
    form,
    setForm,
  ] = useState({
    name: "",
    description: "",
    isPublic: false,
  });

  const load =
    useCallback(async () => {
      const query =
        buildQuery({
          name,
          owner,
          sortBy,
          direction,
        });

      try {
        const data =
          await api(
            `/api/collections?${query}`
          );

        setCollections(
          Array.isArray(data)
            ? data
            : []
        );

        setMessage("");

      } catch (error) {
        setCollections([]);

        setMessage(
          `${error.message} — log in to manage collections.`
        );
      }
    }, [
      name,
      owner,
      sortBy,
      direction,
    ]);

  useEffect(() => {
    load();
  }, [load]);

  async function createCollection(
    event
  ) {
    event.preventDefault();

    try {
      await api(
        "/api/collections",
        {
          method: "POST",
          body: JSON.stringify({
            name: form.name,
            description:
              form.description,
            public:
              form.isPublic,
          }),
        }
      );

      setForm({
        name: "",
        description: "",
        isPublic: false,
      });

      await load();

    } catch (error) {
      setMessage(
        error.message
      );
    }
  }

  async function editCollection(
    collection
  ) {
    const newName =
      window.prompt(
        "Collection name",
        collection.name
      );

    if (!newName) {
      return;
    }

    const description =
      window.prompt(
        "Description",
        collection.description ||
          ""
      );

    try {
      await api(
        `/api/collections/${collection.id}`,
        {
          method: "PUT",
          body: JSON.stringify({
            ...collection,
            name: newName,
            description:
              description ??
              collection.description,
            public:
              Boolean(
                collection.public
              ),
          }),
        }
      );

      await load();

    } catch (error) {
      setMessage(
        error.message
      );
    }
  }

  async function removeCollection(
    id
  ) {
    if (!window.confirm(
      "Delete this collection?"
    )) {
      return;
    }

    try {
      await api(
        `/api/collections/${id}`,
        {
          method: "DELETE",
        }
      );

      await load();

    } catch (error) {
      setMessage(
        error.message
      );
    }
  }

  async function addItem(
    collectionId
  ) {
    const itemId =
      window.prompt(
        "Enter the Media Item ID to add:"
      );

    if (!itemId) {
      return;
    }

    try {
      await api(
        `/api/collections/${collectionId}/items`,
        {
          method: "POST",
          body: JSON.stringify({
            itemId:
              Number(itemId),
          }),
        }
      );

      setMessage(
        "Item added."
      );

    } catch (error) {
      setMessage(
        error.message
      );
    }
  }

  async function viewItems(
    collectionId
  ) {
    try {
      const items =
        await api(
          `/api/collections/${collectionId}/items`
        );

      const list =
        Array.isArray(items) &&
        items.length
          ? items
              .map((item) =>
                `${item.id}: ${item.title}`
              )
              .join("\n")
          : "No items.";

      window.alert(list);

    } catch (error) {
      setMessage(
        error.message
      );
    }
  }

  return (
    <section>
      <h1>
        Collections
      </h1>

      <form
        className="form-card"
        onSubmit={
          createCollection
        }
      >
        <h2>
          Create collection
        </h2>

        <input
          placeholder="Collection name"
          value={form.name}
          onChange={(event) =>
            setForm({
              ...form,
              name:
                event.target.value,
            })
          }
          required
        />

        <textarea
          placeholder="Description"
          value={
            form.description
          }
          onChange={(event) =>
            setForm({
              ...form,
              description:
                event.target.value,
            })
          }
        />

        <label className="checkbox">
          <input
            type="checkbox"
            checked={
              form.isPublic
            }
            onChange={(event) =>
              setForm({
                ...form,
                isPublic:
                  event.target.checked,
              })
            }
          />

          Public
        </label>

        <button type="submit">
          Create collection
        </button>
      </form>

      <div className="toolbar">
        <input
          placeholder="Search name"
          value={name}
          onChange={(event) =>
            setName(
              event.target.value
            )
          }
        />

        <input
          placeholder="Owner"
          value={owner}
          onChange={(event) =>
            setOwner(
              event.target.value
            )
          }
        />

        <select
          value={sortBy}
          onChange={(event) =>
            setSortBy(
              event.target.value
            )
          }
        >
          <option value="createdDate">
            Date
          </option>

          <option value="name">
            Name
          </option>

          <option value="owner">
            Owner
          </option>
        </select>

        <select
          value={direction}
          onChange={(event) =>
            setDirection(
              event.target.value
            )
          }
        >
          <option value="asc">
            Ascending
          </option>

          <option value="desc">
            Descending
          </option>
        </select>
      </div>

      {message && (
        <p className="message">
          {message}
        </p>
      )}

      <div className="card-grid">
        {collections.map(
          (collection) => (
            <article
              className="card"
              key={
                collection.id
              }
            >
              <h2>
                {collection.name}
              </h2>

              <p>
                {
                  collection.description
                }
              </p>

              <p className="muted">
                Owner:
                {" "}
                {
                  collection.owner
                }
                {" · "}
                {
                  visibilityLabel(
                    collection.public
                  )
                }
              </p>

              <div className="button-row">
                <button
                  onClick={() =>
                    viewItems(
                      collection.id
                    )
                  }
                >
                  View items
                </button>

                <button
                  onClick={() =>
                    addItem(
                      collection.id
                    )
                  }
                >
                  Add item
                </button>

                <button
                  onClick={() =>
                    editCollection(
                      collection
                    )
                  }
                >
                  Edit
                </button>

                <button
                  className="danger"
                  onClick={() =>
                    removeCollection(
                      collection.id
                    )
                  }
                >
                  Delete
                </button>
              </div>
            </article>
          )
        )}
      </div>
    </section>
  );
}
