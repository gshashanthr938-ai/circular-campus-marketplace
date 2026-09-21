# CampusMarket handoff

Workspace: `C:\Hero\WEDASS`

CampusMarket is a Java 17 Servlet/JSP/JDBC PBL project. It runs on embedded Tomcat with H2 by default and supports MySQL through environment variables. The interface uses an indigo/violet theme, real local product photography and responsive layouts.

## Current features

- Guest cookie cart, login session rotation and cart migration.
- Seller listings with required one-to-three JPG/PNG/WebP uploads (3 MB maximum per picture).
- UPI or net-banking checkout. No signup credit or wallet balance is displayed or used.
- Academic payment simulation records method, status and a unique reference without collecting bank passwords, OTPs or account numbers.
- Atomic stock claim: the first completed checkout wins; a competing buyer is waitlisted and notified.
- Administrator moderation, image selection, listing state and availability notifications.
- Category/condition price limits, prohibited-item blocking, verified reviews and ratings.
- Seller trust profiles show membership date, completed sales and aggregate ratings without exposing contact details while browsing.
- Checkout records mandatory terms acceptance; completed buyers receive the seller's email and phone for pickup coordination.
- PBKDF2 passwords, CSRF tokens and prepared SQL.

## Run and test

Run `run.cmd` on Windows and open `http://localhost:8080/browse`. Run `mvnw.cmd test` for Java tests. `tools/smoke-test.mjs` performs the disposable browser-level flow when `TEST_BASE_URL` is set.

Demo accounts all use password `password`: `asha@campus.edu` (administrator), `rahul@campus.edu`, and `neha@campus.edu`.

## Replit and GitHub

`.replit` and `replit.nix` are included. Import the GitHub repository into Replit and press Run. Use a persistent MySQL database for a deployed app; H2 and local uploads are suitable for a workspace demonstration.

The repository has no GitHub remote and GitHub CLI is not authenticated. Before pushing, authenticate the student's GitHub account, configure their real Git author name/email, create a public repository, and push the current branch. Never commit `data/`, uploaded seller files, database passwords or `.env` files.

## Accuracy note

The PBL title still contains “Wallet,” but the implemented checkout follows the latest project requirement: UPI and net banking only. A real payment gateway still requires merchant credentials and signed webhook verification.
