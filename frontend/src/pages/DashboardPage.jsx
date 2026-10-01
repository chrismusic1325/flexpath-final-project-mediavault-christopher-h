import React, {
  useEffect,
  useState,
} from "react";

import {
  api,
  getUsername,
} from "../api";

export default function DashboardPage() {
  const [items, setItems] =
    useState([]);

  const [
    collections,
    setCollections,
  ] = useState([]);

  useEffect(() => {
    Promise.all([
      api("/api/items/public"),
      api("/api/collections/public"),
    ])
      .then(([
        itemData,
        collectionData,
      ]) => {
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
      })
      .catch(() => {
        setItems([]);
        setCollections([]);
      });
  }, []);

  return (
    <section>
      <div className="hero">
        <p className="eyebrow">
          CREATE · CURATE · RETRIEVE
        </p>

        <h1>
          MediaVault
        </h1>

        <p>
          Organize music, movies,
          books, articles, videos,
          and other media into
          private or public
          collections.
        </p>
      </div>

      <div className="card-grid">
        <article className="card">
          <h2>
            Public items
          </h2>

          <strong>
            {items.length}
          </strong>
        </article>

        <article className="card">
          <h2>
            Public collections
          </h2>

          <strong>
            {collections.length}
          </strong>
        </article>

        <article className="card">
          <h2>
            Current user
          </h2>

          <strong>
            {getUsername() ||
              "Guest"}
          </strong>
        </article>
      </div>
    </section>
  );
}
