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
} from "../utils";

export default function PublicLibraryPage() {
  const [
    items,
    setItems,
  ] = useState([]);

  const [
    collections,
    setCollections,
  ] = useState([]);

  const [
    search,
    setSearch,
  ] = useState("");

  const [
    message,
    setMessage,
  ] = useState("");

  const load =
    useCallback(async () => {
      try {
        const itemQuery =
          buildQuery({
            title: search,
            sortBy: "title",
            direction: "asc",
          });

        const collectionQuery =
          buildQuery({
            name: search,
            sortBy: "name",
            direction: "asc",
          });

        const [
          itemData,
          collectionData,
        ] =
          await Promise.all([
            api(
              `/api/items/public?${itemQuery}`
            ),

            api(
              `/api/collections/public?${collectionQuery}`
            ),
          ]);

        setItems(
          Array.isArray(itemData)
            ? itemData
            : []
        );

        setCollections(
          Array.isArray(
            collectionData
          )
            ? collectionData
            : []
        );

        setMessage("");

      } catch (error) {
        setMessage(
          error.message
        );
      }
    }, [search]);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <section>
      <h1>
        Public Library
      </h1>

      <div className="toolbar">
        <input
          placeholder="Search public library"
          value={search}
          onChange={(event) =>
            setSearch(
              event.target.value
            )
          }
        />
      </div>

      {message && (
        <p className="message">
          {message}
        </p>
      )}

      <h2>
        Public Items
      </h2>

      <div className="card-grid">
        {items.map((item) => (
          <article
            className="card"
            key={item.id}
          >
            <span className="badge">
              {item.mediaType}
            </span>

            <h3>
              {item.title}
            </h3>

            <p>
              {item.description}
            </p>

            <p className="muted">
              By
              {" "}
              {
                item.creator ||
                "Unknown"
              }
              {" · "}
              Owner
              {" "}
              {item.owner}
            </p>
          </article>
        ))}
      </div>

      <h2 className="section-title">
        Public Collections
      </h2>

      <div className="card-grid">
        {collections.map(
          (collection) => (
            <article
              className="card"
              key={
                collection.id
              }
            >
              <h3>
                {
                  collection.name
                }
              </h3>

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
              </p>
            </article>
          )
        )}
      </div>
    </section>
  );
}
