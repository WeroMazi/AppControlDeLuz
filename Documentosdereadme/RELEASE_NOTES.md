# Release Notes - v1.1.0

## Highlights
- Professionalized room controls with power button, slider levels, and visual feedback.
- Better startup UX with onboarding and privacy notice.
- Improved resilience through cached light state fallback.
- Added analytics and crash reporting hooks for production monitoring.

## Quality
- New unit tests for `AppViewModel` optimistic updates and rollback behavior.
- New Compose UI test for dashboard rendering.
- CI pipeline now validates lint, unit tests, release APK and Play AAB generation.

## Play Store Readiness
- Release minification and resource shrinking enabled.
- Signing supports secure env-based keystore variables.
- Play artifact generation via `bundleRelease` included in CI.
