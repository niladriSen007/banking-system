---
applyTo: "src/**/*.{ts,tsx}"
---

# Frontend Instructions

## Stack and Boundaries
- React 19 + TypeScript + Vite. Use the existing shadcn primitives (`@base-ui/react`, `cva`, and `cn`); do not add another UI, form, state, or HTTP library.
- Feature code belongs under `src/features/<feature>/`: `components/`, `hooks/`, `services/`, `store/`, `types/`, and `utils/` as needed. Follow the nearest existing feature when conventions differ.
- Data flow is component → TanStack Query hook → feature service → `GET`/`POST`/`PUT`/`PATCH`/`DELETE` from `@/lib/apiMethods`. Never call Axios directly from a feature component or hook.
- Routes belong in `src/routes/router.tsx`; use the existing route guards. Keep server state in TanStack Query, cross-feature client state in Zustand, and local presentation state in the component.

## Forms and Types
- Use TanStack Form and Zod. Keep defaults, schemas, and `formOptions(...)` in feature `types/`; use typed field metadata from `utils/` when it fits the form.
- Use the shared `Field`, `FieldLabel`, and `Input` primitives. Keep strict typing, use `import type` for type-only imports, prefer `interface` for object shapes, and do not use `any`.

## Copy, Styling, and Components
- Put all user-facing and accessibility copy (headings, labels, placeholders, actions, validation, and status text) in `src/locales/en.json`. Import it where rendered; do not hardcode or duplicate copy in JSX or feature constants.
- Style pages and components with Tailwind utility classes in `className` only. Do not add component-specific CSS/SCSS, CSS modules, style blocks, or inline style objects.
- Keep page composition and one-off subcomponents in the existing feature component file. Create a separate component only when it is genuinely reusable/shared or explicitly requested. Reuse shared UI from `src/components/ui/` and `src/components/common/`.
- Keep business, fetching, and mutation logic out of presentational components. Surface errors; do not leave `console.log` in hooks or services.