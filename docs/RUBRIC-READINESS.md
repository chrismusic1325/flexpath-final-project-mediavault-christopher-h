# MediaVault Rubric Readiness Report

Generated: Thu Oct  1 00:16:53 EDT 2026

Branch: `mediavault-rubric-hardening-20261001-000853`

## Automated gates

| Gate | Result |
| --- | --- |
| Backend Maven verify | PASS |
| Backend measured line coverage | 98.07% |
| npm dependency install | PASS |
| Frontend Vite build | PASS |
| Frontend Jest + 70% coverage gate | FAIL |
| Frontend measured line coverage | 10.52% |
| Frontend ESLint | FAIL |
| Git diff check | PASS |
| MySQL recreate check | SKIPPED - mysql command not installed |

## Manual rubric gates still required

A script cannot verify these grading requirements:

- Run the complete app against MySQL and manually demonstrate the browser workflow.
- Verify user CRUD, admin CRUD, public/private behavior, collection membership, search, sort, and error cases end-to-end.
- Record the required 15–25 minute walkthrough video.
- Be able to explain the submitted Java, React, SQL, authorization, IDs, architecture, and improvement choices.

## Interpretation

The repository-side automated target is PASS only if backend verify,
frontend install/build/test/lint, coverage gates, and Git diff checks all pass.

Even an automated PASS cannot guarantee a grader's 100% score because
the rubric also contains evaluator-judged quality/understanding criteria
and the required walkthrough video.
