# ♻️ Circular Campus Marketplace — Resale, Cart & Wallet

**PBL2 · Web Technologies** — a peer-to-peer campus resale/thrift platform built with
**Java Servlets, Cookies & HttpSession, and JDBC**.

Students list second-hand items (books, gadgets, hostel essentials) for resale, browse and
search listings, add items to a cart, and check out using an in-app **wallet** balance.
**Guests** can browse and build a cart via **cookies**; when they log in, that cart is
**migrated into their server-side session**.

---

## ✨ Features (mapped to the PBL brief)

| Module | What it does |
| --- | --- |
| **Listings** | Create, edit and remove a resale listing (category, price, condition). |
| **Browse & Search** | Filter listings by text / category / max price. Guest-accessible. A **cookie** remembers *recently viewed* items. |
| **Cart & Wallet** | Session-based cart for logged-in users; checkout deducts the in-app **wallet** balance. |
| **Transaction History** | Separate **buyer** (purchases) and **seller** (sales) views. |

### Cookies vs Sessions — the core learning objective
- 🍪 **Cookies** → guest cart (`guest_cart`) and recently-viewed items (`recently_viewed`). Work without logging in.
- 🔐 **HttpSession** → logged-in identity, the cart (persisted in `cart_items` keyed by session id) and wallet flow.
- 🔄 **Cart migration** → on login, the cookie cart is merged into the session/DB cart and the cookie is cleared.
- 🚪 **Session invalidation** → logout clears the session cart and calls `session.invalidate()`.

### Stretch goals implemented
- 🌱 **Sustainability points** — each buyer earns +10 points per item rehomed, shown on their profile.
- 💸 **Wallet transfer** — money moves from the buyer's wallet to the seller's wallet at checkout.

---

## 🧰 Tech stack
- **Java 17**, **Servlet API 4.0** (`javax.servlet`)
- **Apache Tomcat 9** — *embedded*, so there's nothing extra to install or configure
- **JDBC** with **H2** by default (zero-install, MySQL-compatible) or **MySQL 8** (see below)
- **JSP + JSTL**, **HTML/CSS/Bootstrap 5**
- **Maven** (via the bundled wrapper — no separate Maven install needed)

---

## 🚀 How to run

### Prerequisite
- **JDK 17** installed (e.g. [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17)).
  Everything else (Tomcat, Maven, the database) is bundled or downloaded automatically.

### Start it
On **Windows**, just double-click **`run.cmd`**, or from a terminal:

```bash
run.cmd
```

On **macOS/Linux** (or Git Bash):

```bash
./mvnw -q compile exec:java
```

Then open **<http://localhost:8080/>**. The database is created and seeded automatically on
first launch. Press **Ctrl+C** to stop.

### Demo accounts
Password for all three is `password`:

| Email | Starting wallet |
| --- | --- |
| `asha@campus.edu` | ₹5000 |
| `rahul@campus.edu` | ₹3000 |
| `neha@campus.edu` | ₹1500 |

You can also register a new account (starts with a ₹2000 wallet).

### Try the full flow
1. **Without logging in**, add an item to your cart (it's saved in a cookie).
2. **Log in** — notice the cart follows you in (cookie → session migration).
3. **Checkout** — your wallet is debited, the seller is credited, and the sale appears under **History**.

---

## 🗄️ Using MySQL instead of H2 (optional)
The brief names MySQL; the app speaks plain JDBC so it runs on either. To switch:

1. Install MySQL and note your `root` password.
2. Open [`src/main/resources/db.properties`](src/main/resources/db.properties), comment out the
   four H2 lines, and uncomment the four MySQL lines (set your password). The database and tables
   are created automatically on first run.
3. Start the app as usual.

The portable schema lives in [`src/main/resources/schema.sql`](src/main/resources/schema.sql).

---

## 🧱 Database schema

| Table | Key fields |
| --- | --- |
| `students` | student_id, name, email, password (SHA-256), wallet_balance, sustainability_points |
| `listings` | listing_id, seller_id, title, description, category, price, item_condition, status, created_at |
| `cart_items` | cart_id, **session_id**, listing_id, added_at |
| `transactions` | txn_id, buyer_id, listing_id, amount, txn_date |

---

## 📁 Project structure
```
src/main/java/com/campusmarket/
├── Main.java                 # Embedded Tomcat launcher
├── db/     Db, DbBootstrap   # JDBC connection + schema/seed on startup
├── model/  Student, Listing, TransactionView
├── dao/    StudentDao, ListingDao, CartDao, TransactionDao
├── service/CheckoutService   # Atomic wallet+transaction checkout
├── util/   PasswordUtil, CookieUtil
└── web/    Servlets + CommonAttributesFilter + Web helper
src/main/resources/           # db.properties, schema.sql
src/main/webapp/              # index.jsp, WEB-INF/web.xml, WEB-INF/views/*.jsp, css/
```

See [`docs/REPORT.md`](docs/REPORT.md) for the full design write-up (problem statement,
objectives, design decisions, and how each requirement is met).
