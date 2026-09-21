# CampusMarket — Circular Campus Marketplace

PBL2: Resale, Cart & Wallet · Web Technologies

A campus resale marketplace using Java 17, Servlets, JSP/JSTL and JDBC. Guests build a cookie-based cart, sign in without losing it, and check out using UPI or net banking.

## Features

- Seller-owned listings: create, edit and remove; sold items cannot be edited.
- Search, category and price filters; nine categories and 30 starter listings.
- Locally bundled reference photographs and recently viewed items.
- Guest cookies, login session renewal and cart migration into HttpSession.
- UPI and net-banking checkout with transaction references and sustainability points.
- Sellers upload one to three real product pictures (JPG, PNG or WebP, up to 3 MB each).
- Concurrent checkout protection, CSRF tokens, salted PBKDF2 passwords and prepared SQL statements.
- First-completed-checkout wins when two buyers race for one item; the other buyer is waitlisted and notified when an administrator makes it available again.
- Transaction-linked ratings and reviews shown on each product page.
- Seller trust profiles show membership date, completed sales and ratings; email and phone remain protected until purchase.
- Checkout requires clear purchase terms, records acceptance and unlocks pickup contact details in transaction history.
- Administrator moderation for listing status, notes and local product photographs.
- Transparent price limits by category and condition, plus blocked campus items such as tobacco, vapes, alcohol, weapons and narcotics.

## Run

Install JDK 17. On Windows, double-click `run.cmd`. On macOS/Linux, run `sh mvnw compile exec:java`. Maven and dependencies download on first run.

Open http://localhost:8080/browse. Keep the server running. H2 is the default local preview database, saved under `data/` (excluded from Git).

## MySQL — assignment configuration

The assignment specifies MySQL. The full purchase flow was verified on MySQL 8.0.46. H2 remains the easy preview option; it should not be described as MySQL.

Run `run-mysql.ps1` in PowerShell. It prompts for your MySQL credentials without saving the password. The default connection is localhost:3306/campus_marketplace. The account needs permission to create the database and tables on first run.

Alternatively set these environment variables before starting Maven:

| Variable | Value |
| --- | --- |
| DB_DRIVER | com.mysql.cj.jdbc.Driver |
| DB_URL | jdbc:mysql://localhost:3306/campus_marketplace?createDatabaseIfNotExist=true&serverTimezone=UTC |
| DB_USER | Your MySQL username |
| DB_PASSWORD | Your MySQL password; never commit it |

For local public-key authentication, add `&allowPublicKeyRetrieval=true` to DB_URL. Remote database connections require appropriate TLS configuration. Switching databases creates a separate dataset; it does not import existing H2 purchases.

## Demo accounts

All starter accounts use password `password`. No account receives marketplace money during signup or login.

| Account | Role |
| --- | --- |
| asha@campus.edu | Administrator |
| rahul@campus.edu | Student |
| neha@campus.edu | Student |

Add another student's item as a guest, log in, choose UPI or net banking, and inspect the payment reference in transaction history.

## Architecture

Browser (HTML/CSS/Bootstrap/JSP) → Servlets and session/CSRF filter → Checkout service / DAOs → JDBC → MySQL or H2.

| Table | Stores |
| --- | --- |
| students | Accounts, phone/email contact, password hashes, roles and sustainability points |
| listings | Seller-owned inventory and status |
| listing_images | Up to three seller-uploaded product-image paths |
| cart_items | Session-linked cart entries |
| transactions | Purchases, amounts, payment method, reference, status and terms acceptance |
| waitlist | Students waiting for an unavailable listing |
| notifications | Availability and checkout-conflict alerts |
| reviews | One verified rating/review per completed transaction |
| catalog_updates | One-time catalog installation marker |

## Verification and documentation

Run `mvnw.cmd test` on Windows or `sh mvnw test` elsewhere. Tests use an isolated in-memory database. GitHub Actions runs tests and packaging on Java 17.

- [Validation results](docs/VALIDATION.md)
- [Project report and ER diagram](docs/REPORT.md)
- [Presentation demonstration guide](docs/DEMO.md)
- [Presentation deck](docs/CampusMarket-Presentation.pptx)
- [Photo sources](docs/IMAGE-SOURCES.md)
- [Generated product-image notes](docs/GENERATED-IMAGES.md)

The Node 18+ HTTP checker is `tools/smoke-test.mjs`. Set TEST_BASE_URL to an app using a disposable database: it creates test users, listings and purchases.

## Replit

Import the GitHub repository into Replit and press **Run**. The included `.replit` and `replit.nix` files install Java 17, run Maven and expose port 3000. For a deployment, configure a persistent MySQL database through the `DB_DRIVER`, `DB_URL`, `DB_USER` and `DB_PASSWORD` secrets. The default H2 file and uploaded seller pictures are suitable for a workspace demo, but an autoscale deployment can replace its local filesystem between releases.

## Scope

The checkout is an academic UPI/net-banking gateway simulation: it validates the selected method and records a completed payment reference, but it does not contact a bank or collect passwords, OTPs or account numbers. Connecting a real gateway requires a merchant account, server-side API credentials, signed webhooks, refunds and compliance work. Seller uploads are stored locally and excluded from Git; bundled catalog photos remain in the repository. Student email ownership, pickup coordination and rate limiting remain future work.

GitHub hosts source code. GitHub Pages cannot run this Java backend; public hosting requires a Java server and database. Local data and credentials are excluded from the repository.
