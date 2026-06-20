# Contributing to mavai-stt-bench

Thank you for your interest in contributing. This document describes how to
submit contributions and the legal terms that apply.

This project is designed to be **forked and extended** — adding a corpus, a
recipe, or an STT provider is the expected mode of use. Contributions that
generalise those extension points (without over-generalising the scaffold) are
especially welcome. See the [README](README.md) for the extension guides and
the open Hackergarten tasks.

## License

mavai-stt-bench is licensed under the [Apache License, Version 2.0](LICENSE).
All contributions are accepted under the same license.

## Developer Certificate of Origin

All contributions are subject to the
[Developer Certificate of Origin (DCO)](https://developercertificate.org/).
The DCO text is available verbatim in the [dco.txt](dco.txt) file in the root
of this repository.

By signing off your commits, you certify that you wrote the contribution or
otherwise have the right to submit it under the project's license. No separate
contributor agreement is required.

### Signing your commits

Add a `Signed-off-by` line to every commit message — easiest with the `-s`
flag:

```
git commit -s -m "Your commit message"
```

The name and email must match your git identity (`git config user.name` and
`git config user.email`).

## Reporting issues

Please use [GitHub Issues](https://github.com/mavai-org/mavai-stt-bench/issues)
for bug reports and feature requests. Include a minimal reproducer where
possible.

## Pull requests

- Fork the repository and create a topic branch from `main`.
- Keep changes focused; one logical change per pull request.
- Ensure all commits are signed off (see above).
- Run `./gradlew clean build` locally before opening the pull request.
- Reference any related issue in the pull request description.
