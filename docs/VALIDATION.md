# Validation — 21 September 2026

Java 17 / Maven: 13 automated tests passed, zero failures/errors.

Tests cover UPI and net-banking checkout, payment references, required terms acceptance, invalid-payment rollback, points, repeat checkout, duplicate cart rows, own/sold items, competing buyers, loser waitlisting and notification, price limits, prohibited items, salted PBKDF2 verification and legacy/malformed password hashes.

MySQL 8.0.46 was tested in a separate local instance on port 3307, with the test application on 8090. Existing application data and the installed MySQL service were not reset.

The HTTP checker covers registration, CSRF rejection, session renewal, guest-cart migration, seller photo upload, UPI and net-banking payment, accepted terms, unlocked seller contact in history, cart clearing, competing buyers and waitlist alerts, prohibited-item rejection, invalid-payment rollback, removing items referenced in carts, logout, public pages and image availability.

These are local functional checks, not a production security audit. Real payment processing and public deployment remain outside this submission.
