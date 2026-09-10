# Project Report — Circular Campus Marketplace

**PBL2 · Web Technologies**
**Title:** Circular Campus Marketplace — Resale, Cart & Wallet (Servlets + Cookies & Sessions)

> _Student name / roll no / section:_ **__________________________**
> _(fill this in before submitting)_

---

## 1. Problem statement
Build a *Circular Campus Marketplace* — a peer-to-peer resale/thrift platform (books, gadgets,
hostel essentials) using **Servlets, Cookies, and HttpSession** — where a logged-in student lists
items for resale, browses/searches listings, adds items to a cart, and checks out via a simple
in-app **wallet** balance, with **guest browsing** supported through cookies.

## 2. Why it matters (market context, 2026)
Circular-economy and resale platforms (thrift, refurbished electronics, campus swap markets) are
among the fastest-growing e-commerce categories, driven by sustainability-conscious students.
Universities increasingly run internal marketplaces for textbooks and hostel items. This project
reproduces that product pattern end-to-end at the Servlet layer.

## 3. Learning objectives (and how they are met)
| Objective | Where it's demonstrated |
| --- | --- |
| Apply **cookies vs sessions** correctly | Guest cart + recently-viewed use cookies; logged-in cart + wallet use `HttpSession`. |
| Handle multiple concurrent HTTP request/response flows | Independent servlets for browse, listing, cart, checkout, history. |
| Persist data via **JDBC** | `Db` + DAO classes use `PreparedStatement` for every query. |
| **Session invalidation** on logout | `LogoutServlet` clears the cart and calls `session.invalidate()`. |
| **Cart migration** cookie → session on login | `Web.migrateGuestCartToSession()` runs inside `LoginServlet`. |

## 4. Architecture
A classic **MVC / layered** design on embedded Tomcat:

```
Browser ──HTTP──▶ Servlets (controller)  ──▶ Service / DAO (JDBC)  ──▶ Database
                        │
                        ▼
                 JSP + JSTL (view)
```

- **Controller** — `com.campusmarket.web.*` servlets (annotated with `@WebServlet`).
- **View** — JSP pages under `WEB-INF/views/` using JSTL; a shared header/footer and a
  `CommonAttributesFilter` (a `@WebFilter`) supply the nav bar's cart badge and user info.
- **Model** — plain Java beans (`Student`, `Listing`, `TransactionView`).
- **Data access** — one DAO per table plus a transactional `CheckoutService`.
- **Persistence** — JDBC over H2 (default) or MySQL; identical SQL for both.

## 5. Cookies vs Sessions — design decisions (core of the project)
| Concern | Stored in | Reason |
| --- | --- | --- |
| Recently-viewed items | Cookie `recently_viewed` | Must work for **guests**; low-value, client-side, survives across visits. |
| Guest cart | Cookie `guest_cart` | Lets a not-logged-in visitor build a cart before signing in. |
| Logged-in identity | `HttpSession` attribute `student` | Server-side, trusted, per-session. |
| Logged-in cart | `cart_items` table keyed by **session id** | Durable + matches the brief's schema; tied to the session lifecycle. |
| Wallet state | `students.wallet_balance` (DB), read via session user | Money must be authoritative and server-side. |

**Migration:** when a guest logs in, `migrateGuestCartToSession()` reads the `guest_cart` cookie,
inserts each still-available item into `cart_items` under the new session id, and deletes the
cookie — so the visitor's cart is never lost.

**Logout:** `session.invalidate()` destroys all session state; the session's cart rows are cleared
first so nothing is orphaned.

## 6. Database schema
Four tables (see `src/main/resources/schema.sql`):

- **students**(student_id, name, email, password, wallet_balance, sustainability_points)
- **listings**(listing_id, seller_id→students, title, description, category, price, item_condition, status, created_at)
- **cart_items**(cart_id, session_id, listing_id→listings, added_at)
- **transactions**(txn_id, buyer_id→students, listing_id→listings, amount, txn_date)

`status` on a listing is `AVAILABLE` → `SOLD`. Passwords are stored as SHA-256 hashes, never plain text.

**Relationships:** a student has many listings and many transactions; a listing belongs to one
seller and may appear in many carts and (once) in a transaction.

## 7. Tasks / milestones — completion
1. ✅ Designed schema for Students, Listings, Cart_Items, Transactions.
2. ✅ Login creates an `HttpSession`; guest "recently viewed" uses a cookie.
3. ✅ Listing-creation and browse/search servlets with category/price filtering.
4. ✅ Session-based cart and a checkout servlet that deducts the wallet and writes a Transaction.
5. ✅ Cart migration from cookie-based guest state to session state on login.

## 8. Expected outcomes — achieved
- ✅ A working peer-to-peer resale marketplace with correct guest vs logged-in state handling.
- ✅ Clear demonstration of cookie-based guest tracking vs session-based cart/wallet state.
- ✅ Database-backed listings and transaction history reflecting every purchase.
- ✅ This report covering the session/cookie design decisions.

## 9. Stretch goals — implemented
- 🌱 **Sustainability points**: +10 per item rehomed, shown on the profile page.
- 💸 **Wallet transfer**: checkout moves funds from buyer to seller wallet atomically.

## 10. How to run
Install **JDK 17**, then run `run.cmd` (Windows) or `./mvnw -q compile exec:java`, and open
<http://localhost:8080/>. Demo logins: `asha@campus.edu` / `rahul@campus.edu` / `neha@campus.edu`
(password `password`). Full instructions and the optional MySQL switch are in the
[README](../README.md).

## 11. A full list-to-purchase cycle (what to screenshot for submission)
Capture these steps for your demo (save images into `docs/screenshots/`):
1. **Browse** page with listings and the search/filter bar.
2. Adding an item to the cart **as a guest** (note the cart badge without being logged in).
3. **Login** page (demo accounts).
4. **Cart** page after login showing the migrated item + wallet balance.
5. **Checkout** success message.
6. **History** page showing the purchase, and the **Profile** page showing the reduced wallet
   and sustainability points.

## 12. Possible future improvements
- Password hashing with bcrypt/Argon2 + per-user salt.
- Item images and image upload.
- Ratings/reviews per completed transaction.
- Pagination and full-text search on listings.
- CSRF tokens on state-changing POST forms.
