# Frontend Architecture

## Scope

This repository is the browser-facing React application. Treat backend behavior and banking-domain rules as external contracts unless documented by an authoritative backend source. Do not infer transfer, account, fee, or lifecycle rules from UI labels.

## Runtime and Ownership

- React 19 + TypeScript + Vite; `@/` aliases to `src/`.
- `src/main.tsx` mounts the app. Providers are under `src/provider/`.
- `src/routes/router.tsx` owns the `createBrowserRouter` tree. `CustomerLayout` hosts the main route outlet; public-only and protected layouts guard their route groups.
- TanStack Query owns remote/server state. Zustand owns cross-feature client state (currently authentication). Keep short-lived presentation state local to components.
- shadcn primitives are in `src/components/ui/`; shared layout/common components are in `src/components/`.

## Feature and Request Flow

Feature code is organized under `src/features/<feature>/` as needed:

- `components/`: feature UI
- `hooks/`: TanStack Query queries and mutations
- `services/`: API-facing feature functions
- `store/`: feature-owned Zustand state
- `types/`: request/response types, Zod schemas, form options
- `utils/`: pure helpers and field metadata

For remote data, use component → hook → service → `src/lib/apiMethods.ts` → `src/lib/api.ts`. The shared methods wrap Axios, validate the app's `ApiResponse` success shape, and return its `data`. Do not bypass this boundary from a feature. Authentication currently uses `/auth/login` and `/auth/register`; confirm endpoint payloads and response shapes from backend documentation or an authorized MCP source before changing them.

## UI Conventions

- English user-facing and accessibility copy lives in `src/locales/en.json`.
- Tailwind utility classes in JSX are the styling mechanism. Do not add component-specific stylesheets or inline style objects.
- Keep one-off page composition in its existing feature component. Extract components only for real reuse or an explicit request.
- Forms use TanStack Form + Zod and existing shadcn field/input primitives.

## Change Guidance

Before a structural change, inspect the nearest route, feature, hook, and service that already solve a similar problem. Preserve the request/response boundary and route guards. If local code and backend documentation disagree, do not guess: identify the mismatch and resolve the contract before implementing behavior.