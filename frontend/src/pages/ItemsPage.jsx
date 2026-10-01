import {
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

const emptyForm = {
  title: "",
  creator: "",
  mediaType: "Music",
  description: "",
  isPublic: false,
};

export default function ItemsPage() {
  const [
    items,
    setItems,
  ] = useState([]);

  const [
    form,
    setForm,
  ] = useState(emptyForm);

  const [
    title,
    setTitle,
  ] = useState("");

  const [
    creator,
    setCreator,
  ] = useState("");

  const [
    mediaType,
    setMediaType,
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

  const load =
    useCallback(async () => {
      const query =
        buildQuery({
          title,
          creator,
          mediaType,
          sortBy,
          direction,
        });

      try {
        const data =
          await api(
            `/api/items?${query}`
          );

        setItems(
          Array.isArray(data)
            ? data
            : []
        );

        setMessage("");

      } catch (error) {
        setItems([]);

        setMessage(
          `${error.message} — log in to manage private items.`
        );
      }
    }, [
      title,
      creator,
      mediaType,
      sortBy,
      direction,
    ]);

  useEffect(() => {
    load();
  }, [load]);

  function updateForm(
    field,
    value
  ) {
    setForm((current) => ({
      ...current,
      [field]: value,
    }));
  }

  async function createItem(
    event
  ) {
    event.preventDefault();

    try {
      await api(
        "/api/items",
        {
          method: "POST",
          body: JSON.stringify({
            title:
              form.title,

            creator:
              form.creator,

            mediaType:
              form.mediaType,

            description:
              form.description,

            public:
              form.isPublic,
          }),
        }
      );

      setForm(
        emptyForm
      );

      await load();

    } catch (error) {
      setMessage(
        error.message
      );
    }
  }

  async function editItem(
    item
  ) {
    const newTitle =
      window.prompt(
        "Title",
        item.title
      );

    if (!newTitle) {
      return;
    }

    const newDescription =
      window.prompt(
        "Description",
        item.description || ""
      );

    try {
      await api(
        `/api/items/${item.id}`,
        {
          method: "PUT",
          body: JSON.stringify({
            ...item,
            title: newTitle,
            description:
              newDescription ??
              item.description,
            public:
              Boolean(
                item.public
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

  async function removeItem(
    id
  ) {
    if (!window.confirm(
      "Delete this item?"
    )) {
      return;
    }

    try {
      await api(
        `/api/items/${id}`,
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

  return (
    <section>
      <h1>
        Media Items
      </h1>

      <form
        className="form-card"
        onSubmit={createItem}
      >
        <h2>
          Create item
        </h2>

        <div className="form-grid">
          <input
            placeholder="Title"
            value={form.title}
            onChange={(event) =>
              updateForm(
                "title",
                event.target.value
              )
            }
            required
          />

          <input
            placeholder="Creator"
            value={form.creator}
            onChange={(event) =>
              updateForm(
                "creator",
                event.target.value
              )
            }
          />

          <select
            value={
              form.mediaType
            }
            onChange={(event) =>
              updateForm(
                "mediaType",
                event.target.value
              )
            }
          >
            <option>
              Music
            </option>

            <option>
              Movie
            </option>

            <option>
              Book
            </option>

            <option>
              Video
            </option>

            <option>
              Article
            </option>

            <option>
              Podcast
            </option>

            <option>
              Note
            </option>

            <option>
              Other
            </option>
          </select>
        </div>

        <textarea
          placeholder="Description"
          value={
            form.description
          }
          onChange={(event) =>
            updateForm(
              "description",
              event.target.value
            )
          }
        />

        <label className="checkbox">
          <input
            type="checkbox"
            checked={
              form.isPublic
            }
            onChange={(event) =>
              updateForm(
                "isPublic",
                event.target.checked
              )
            }
          />

          Public
        </label>

        <button type="submit">
          Create item
        </button>
      </form>

      <div className="toolbar">
        <input
          placeholder="Search title"
          value={title}
          onChange={(event) =>
            setTitle(
              event.target.value
            )
          }
        />

        <input
          placeholder="Search creator"
          value={creator}
          onChange={(event) =>
            setCreator(
              event.target.value
            )
          }
        />

        <input
          placeholder="Media type"
          value={mediaType}
          onChange={(event) =>
            setMediaType(
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

          <option value="title">
            Title
          </option>

          <option value="creator">
            Creator
          </option>

          <option value="mediaType">
            Media type
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
        {items.map((item) => (
          <article
            className="card"
            key={item.id}
          >
            <span className="badge">
              {item.mediaType}
            </span>

            <h2>
              {item.title}
            </h2>

            <p>
              <strong>
                Creator:
              </strong>{" "}
              {item.creator ||
                "Unknown"}
            </p>

            <p>
              {item.description}
            </p>

            <p className="muted">
              Owner: {item.owner}
              {" · "}
              {
                visibilityLabel(
                  item.public
                )
              }
            </p>

            <div className="button-row">
              <button
                onClick={() =>
                  editItem(item)
                }
              >
                Edit
              </button>

              <button
                className="danger"
                onClick={() =>
                  removeItem(
                    item.id
                  )
                }
              >
                Delete
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
