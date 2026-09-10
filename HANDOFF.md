# 🤝 Project Handoff — Circular Campus Marketplace (PBL2)

> **Read me first.** This file is written for the next AI assistant session (on a new
> account) **and** for the student. It captures everything done so far, how to run it,
> and exactly what is left. The whole project lives at **`C:\Hero\WEDASS`** on this
> Windows 11 machine and is unchanged between accounts — only the AI's memory resets,
> so this document is the source of truth.

---

## 1. What this project is
A university **PBL2 assignment** (Web Technologies): **"Circular Campus Marketplace —
Resale, Cart & Wallet (Servlets + Cookies & Sessions)."** It's a peer-to-peer campus
resale website where students list second-hand items, browse as guests (cart saved in a
**cookie**), log in (cart migrates into the **session** + DB), and check out using an
in-app **wallet**. The full assignment brief text is summarised in
[`docs/REPORT.md`](docs/REPORT.md).

The app is branded **"CampusMarket"** in the UI.

---

## 2. Current status — ✅ everything works
- The complete Java Servlet web app is **built, compiles, runs, and was tested
  end-to-end** (guest cookie cart → login migration → wallet checkout → history →
  wallet correctly debited).
- The UI was **redesigned** (see §7) to an indigo/violet theme with illustrated product
  images — deliberately more polished than a reference site the student shared
  ("Campus Circle").
- A **PowerPoint deck** exists: [`docs/Circular-Campus-Marketplace.pptx`](docs/Circular-Campus-Marketplace.pptx)
  (12 slides). ⚠️ It still uses the **old green** theme — see §11 "What's left".
- Git repo initialised and a checkpoint commit made (see §9). **Not yet pushed to GitHub.**

---

## 3. How to run it (the important part)
**Prerequisite already installed on this machine:** Temurin **JDK 17** at
`C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot`.

**Easiest (Windows):** double-click **`run.cmd`** in `C:\Hero\WEDASS`, then open
<http://localhost:8080/>.

**From the Bash tool (what the AI should use):**
```bash
cd /c/Hero/WEDASS
export JAVA_HOME="C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.20.101-hotspot"
export PATH="/c/Hero/tools/apache-maven-3.9.9/bin:$PATH"
mvn -q compile exec:java    # or: ./mvnw -q compile exec:java
```
Runs an **embedded Tomcat 9** on port 8080 (no separate Tomcat needed). The database
(H2, embedded) is created and seeded automatically on first run. Press Ctrl+C / kill the
process to stop. When running it in the background, wait for the log line
`Circular Campus Marketplace is running!`.

---

## 4. Toolchain installed on this machine (persists across accounts)
| Tool | Location / note |
|---|---|
| **JDK 17** (Temurin) | `C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot` — set as `JAVA_HOME` |
| **Maven 3.9.9** | `C:\Hero\tools\apache-maven-3.9.9\` (downloaded manually; not on PATH by default). Also usable via the committed **Maven Wrapper** `./mvnw`. |
| **GitHub CLI** (`gh`) | Installed at `C:\Program Files\GitHub CLI\gh.exe` — **not yet authenticated** (see §11). |
| **git** | Available (`git version 2.55`). |
| **node/npm** | v24 — used only to build the PPTX deck (`pptxgenjs`). |
| ❌ **Python** | **Not installed.** (The pptx/pdf skills' Python QA scripts won't run here.) |
| ❌ LibreOffice / `pdftoppm` | Not installed — can't render slides/PDF to images on this machine. |

---

## 5. Tech stack
Java 17 · Servlet API 4.0 (`javax.servlet`) · embedded Apache Tomcat 9 · JDBC · **H2**
(default, zero-install) or **MySQL 8** (optional switch) · JSP + JSTL · HTML/CSS +
Bootstrap 5 (CDN) · Maven.

---

## 6. Project structure & key files
```
C:\Hero\WEDASS\
├─ run.cmd, mvnw, mvnw.cmd, pom.xml
├─ README.md                     # user-facing readme
├─ HANDOFF.md                    # THIS FILE
├─ docs/
│  ├─ REPORT.md                  # full project report (fill in name/roll no)
│  ├─ Circular-Campus-Marketplace.pptx   # slide deck (old green theme)
│  └─ screenshots/               # empty — add demo screenshots here
├─ tools/presentation/deck.js    # pptxgenjs generator for the deck (see §11)
└─ src/main/
   ├─ java/com/campusmarket/
   │  ├─ Main.java               # embedded Tomcat launcher
   │  ├─ db/         Db.java, DbBootstrap.java   # JDBC conn + schema/seed on startup
   │  ├─ model/      Student, Listing (has getImage()/getConditionClass()), TransactionView
   │  ├─ dao/        StudentDao, ListingDao, CartDao, TransactionDao
   │  ├─ service/    CheckoutService.java        # atomic wallet+txn checkout
   │  ├─ util/       PasswordUtil (SHA-256), CookieUtil
   │  └─ web/        *Servlet.java, CommonAttributesFilter, Web.java (helpers)
   ├─ resources/     db.properties, schema.sql
   └─ webapp/
      ├─ index.jsp, WEB-INF/web.xml, WEB-INF/views/*.jsp
      ├─ css/style.css           # the theme (indigo/violet)
      └─ img/*.svg               # logo, hero, product & category illustrations
```

Servlets/URLs: `/browse` (home), `/listing?id=`, `/sell`, `/my-listings`, `/cart`,
`/checkout`, `/history`, `/profile`, `/login`, `/register`, `/logout`.

---

## 7. The UI redesign (done — student's explicit requirements)
The student disliked the first version. Current design **must keep** these choices:
- **NO green, NO recycle symbol.** Palette is **indigo `#4f46e5` + violet `#7c3aed`**
  with an **amber `#f59e0b`** accent, on light slate neutrals. Defined as CSS variables
  at the top of `css/style.css`.
- **Brand:** "CampusMarket" with a shopping-bag logo (`img/logo.svg`).
- **Pictures everywhere:** hero illustration (`img/hero.svg`), custom **SVG product
  illustrations** per item (books, calculator, table, lamp, drawing, headphones + category
  fallbacks electronics/furniture/hostel/clothing/sports/default), category icons
  (`img/ic-*.svg`). All local so they never break offline.
- Homepage (`browse.jsp`) has: hero + CTAs → **Browse categories** → Featured product
  cards (image + condition badge + wishlist heart) → **Why CampusMarket** (about) →
  **How CampusMarket works** → rich footer.
- `Listing.getImage()` maps a listing to its illustration; `getConditionClass()` →
  badge colour.

⚠️ **CSS caching gotcha:** the in-app browser caches `style.css` hard. When you edit the
CSS, bump the version in `_header.jsp` (`style.css?v=2` → `?v=3`) or you'll keep seeing
the old styles.

---

## 8. Demo accounts & flow
Login with any (password = `password` for all): `asha@campus.edu` (₹5000),
`rahul@campus.edu` (₹3000), `neha@campus.edu` (₹1500). New sign-ups start with ₹2000.

**Demo flow to show:** add an item to cart **while logged out** (cookie) → **log in**
(cart migrates) → **Cart** → **Checkout** (wallet debited, seller credited) → **History**
+ **Profile** (wallet down, sustainability points up).

---

## 9. Git status
- Repo initialised at `C:\Hero\WEDASS`. Branch: `master`.
- A checkpoint commit was made with a **placeholder identity** (email
  `imaginarymanmovie@gmail.com`, name "CampusMarket Student").
- **`data/`, `target/`, logs are git-ignored** (the DB re-seeds itself).

**Set the real author before pushing** (do this in the new session):
```bash
cd /c/Hero/WEDASS
git config user.name "REAL NAME"
git config user.email "GITHUB_EMAIL"
# optional: fix the existing commit's author
git commit --amend --reset-author --no-edit
```

---

## 10. Database
Default is **H2** (file `./data/…`, auto-created, MySQL-compatible mode) — nothing to
install. To use **MySQL** (as the brief names): edit
[`src/main/resources/db.properties`](src/main/resources/db.properties) — comment the 4
H2 lines, uncomment the 4 MySQL lines, set the password; the DB/tables auto-create.
Portable DDL is in `src/main/resources/schema.sql`.

---

## 11. ⏭️ What's LEFT to do (pick up here)
1. **Push to GitHub** (the student wants this for marks; needs THEIR account):
   - GitHub CLI is installed but not logged in. The student must run, in a terminal:
     ```bash
     "/c/Program Files/GitHub CLI/gh.exe" auth login --hostname github.com --git-protocol https --web
     ```
     (choose GitHub.com → HTTPS → login with browser, paste the one-time code). They need
     a GitHub account first.
   - Then set the real git identity (§9) and create + push the repo:
     ```bash
     cd /c/Hero/WEDASS
     "/c/Program Files/GitHub CLI/gh.exe" repo create circular-campus-marketplace --public --source=. --push
     ```
   - Then put the repo URL into slide 12 of the deck and `README`.

2. **Rebuild the slide deck to the NEW indigo theme** (currently still green):
   - Generator: [`tools/presentation/deck.js`](tools/presentation/deck.js). To rebuild:
     ```bash
     cd /c/Hero/WEDASS/tools/presentation
     npm init -y && npm install pptxgenjs
     node deck.js      # writes ../../docs/Circular-Campus-Marketplace.pptx
     ```
   - Change the palette constants near the top of `deck.js` (`FOREST`/`MOSS` → indigo
     `4f46e5` / violet `7c3aed` / amber `f59e0b`), remove the ♻ glyphs, and align wording
     with the new "CampusMarket" brand and features. No Python/LibreOffice here, so QA the
     deck by opening it in PowerPoint.

3. **Fill in personal details:** student name + roll no on **slide 1** of the deck and at
   the top of `docs/REPORT.md`.

4. **Add screenshots** of the running app into `docs/screenshots/` for the report/slides
   (the app must be running — see §3 — then capture browse, cart, login, checkout, history).

5. *(Optional stretch)* item photo upload, ratings/reviews, pagination, bcrypt hashing.

---

## 12. Gotchas / troubleshooting
- **PATH with spaces:** `C:\Program Files\...` breaks the Bash `PATH`. Don't put the JDK on
  PATH — set `JAVA_HOME` and let Maven use it (Maven lives at the space-free
  `C:\Hero\tools\...`).
- **Port 8080 already in use / stale server:** `taskkill //F //IM java.exe` then retry.
- **Embedded Tomcat classloader:** `Main.java` calls
  `ctx.setParentClassLoader(Main.class.getClassLoader())` — required, or web.xml parsing
  fails under `mvn exec:java`. Don't remove it.
- **CSS not updating in the browser:** bump `style.css?v=N` in `_header.jsp` (cache).
- **JSP/CSS/images** are served live from `src/main/webapp` (no rebuild needed); **Java
  changes** need `mvn compile` + restart.

---

## 13. Note for the next AI session
The project is complete and working — your job is the **§11 checklist**, chiefly (1)
helping the student push to GitHub and (2) rebuilding the deck to match the new indigo UI.
Keep the design constraints in §7 (no green, no recycle symbol, keep the pictures). Run
the app per §3 to see the current state before changing anything.
