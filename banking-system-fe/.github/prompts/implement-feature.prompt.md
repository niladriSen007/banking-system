---
name: implement-feature
description: Implement a React feature in banking-system-fe using the established architecture.
argument-hint: Describe the behavior, acceptance criteria, and any known API contract.
agent: agent
---

Implement this feature request: ${input:feature_request}

Use the applicable repository instructions. Read `docs/architecture.md` only if the change crosses routing, state, feature, or API boundaries. Inspect the closest existing implementation before editing.

Keep the change minimal and complete. Use the established component → hook → service → `apiMethods` flow for remote data, `src/locales/en.json` for all UI/accessibility copy, and Tailwind classes only for styling do not write custom color codes in the codebase, always create that in the `index.css` file and import from there. Do not create one-off component or stylesheet files.

Use shadcn/ui components for consistent design patterns and styling instead of creating your own UI components.

Use MCP only if a specific external contract is missing locally; query the narrowest authoritative source and verify the result. Do not assume business rules or expose secrets.

Run `pnpm lint` after code changes and `pnpm build` for structural/type changes. In the final response, summarize changed files, validation, and unresolved assumptions.