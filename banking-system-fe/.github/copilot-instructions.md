# Copilot Instructions

- Use **pnpm**. Run `pnpm lint` after code changes and `pnpm build` after structural or type changes.
- For React/TypeScript files, follow `.github/instructions/frontend.instructions.md`.
- Read `docs/architecture.md` when a task changes feature boundaries, routing, state ownership, or API integration.
- For feature implementation, use `.github/prompts/implement-feature.prompt.md` or select the `react-architect` agent for architecture-heavy work.
- Use MCP tools only when they provide a specific missing fact (for example, an API contract). Make narrow queries, verify important results against project code or authoritative sources, and do not request, expose, or store secrets in prompts or generated files.
- Keep investigation scoped to the requested behavior and its nearest implementation/test. Report pre-existing validation failures separately from failures introduced by the change.
