# Project workflow

- The user has authorized automatic commits and pushes for this project. After completing each coherent change and its relevant validation, commit the intended changes and push the current branch to its existing GitHub remote without asking for confirmation again.
- Check the worktree and remote state first. Preserve unrelated user changes, never force-push, and report any authentication or merge blocker accurately.
- Keep generated build outputs, local SDK paths, signing keys, and secrets out of Git. Deliberately selected UI screenshots for documentation may be committed.
- Report the pushed branch and commit in the final response. If no files changed, do not create an empty commit.
