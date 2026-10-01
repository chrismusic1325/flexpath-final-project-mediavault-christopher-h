export function buildQuery(values) {
  const params =
    new URLSearchParams();

  Object.entries(values)
    .filter(([, value]) =>
      value !== undefined &&
      value !== null &&
      String(value).trim() !== "")
    .forEach(([key, value]) =>
      params.set(key, String(value)));

  return params.toString();
}

export function visibilityLabel(
  isPublic
) {
  return isPublic
    ? "Public"
    : "Private";
}
