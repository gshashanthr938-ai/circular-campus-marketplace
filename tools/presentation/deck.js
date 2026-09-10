const pptxgen = require("pptxgenjs");
const pres = new pptxgen();
pres.layout = "LAYOUT_WIDE"; // 13.3 x 7.5
pres.author = "Circular Campus Marketplace";
pres.title = "Circular Campus Marketplace";

// ---- Palette (Forest & Moss — circular economy theme) ----
const FOREST = "2C5F2D";
const FOREST_DK = "17331A";
const MOSS = "6FA663";
const MOSS_LT = "C9E0B4";
const CREAM = "F4F7EF";
const INK = "23281F";
const SLATE = "5B6657";
const AMBER = "E0A93B";
const WHITE = "FFFFFF";

const SANS = "Calibri";
const SERIF = "Cambria";
const W = 13.33, H = 7.5, M = 0.6;

// fresh shadow object each call (pptxgenjs mutates in place)
const shadow = (blur = 9, off = 3, op = 0.18) => ({
  type: "outer", color: "1A2617", blur, offset: off, angle: 90, opacity: op,
});

function card(slide, x, y, w, h, fill = WHITE, radius = 0.12) {
  slide.addShape(pres.ShapeType.roundRect, {
    x, y, w, h, rectRadius: radius, fill: { color: fill },
    line: { type: "none" }, shadow: shadow(),
  });
}

function iconCircle(slide, x, y, d, glyph, bg = FOREST, fg = WHITE) {
  slide.addShape(pres.ShapeType.ellipse, { x, y, w: d, h: d, fill: { color: bg }, line: { type: "none" } });
  slide.addText(glyph, {
    x, y, w: d, h: d, align: "center", valign: "middle",
    fontSize: Math.round(d * 22), color: fg, isTextBox: true, margin: 0,
  });
}

function title(slide, text, color = INK, x = M, y = 0.5, w = W - 2 * M) {
  slide.addText(text, {
    x, y, w, h: 0.9, fontFace: SERIF, fontSize: 34, bold: true,
    color, align: "left", valign: "middle", isTextBox: true, margin: 0,
  });
}

function kicker(slide, text, x = M, y = 0.42, color = MOSS) {
  slide.addText(text.toUpperCase(), {
    x, y, w: W - 2 * M, h: 0.3, fontFace: SANS, fontSize: 12.5, bold: true,
    color, charSpacing: 2.5, align: "left", isTextBox: true, margin: 0,
  });
}

// =====================================================================
// SLIDE 1 — Title
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: FOREST_DK };
  // faint recycling glyph motif, large, top-right
  s.addText("\u267B", { x: 8.2, y: -0.6, w: 6, h: 6, fontSize: 300, color: "24471F", align: "center", valign: "middle", isTextBox: true, margin: 0 });
  s.addText("PBL 2  \u00B7  WEB TECHNOLOGIES", {
    x: M, y: 1.7, w: 9, h: 0.4, fontFace: SANS, fontSize: 14, bold: true,
    color: MOSS_LT, charSpacing: 3, isTextBox: true, margin: 0,
  });
  s.addText("Circular Campus\nMarketplace", {
    x: M, y: 2.15, w: 10.5, h: 2.1, fontFace: SERIF, fontSize: 60, bold: true,
    color: WHITE, lineSpacingMultiple: 0.95, isTextBox: true, margin: 0,
  });
  s.addText("Resale, Cart & Wallet  \u2014  buy and sell within campus, sustainably.", {
    x: M, y: 4.35, w: 10.5, h: 0.5, fontFace: SANS, fontSize: 19, color: MOSS_LT, italic: true, isTextBox: true, margin: 0,
  });
  // tech chips
  const chips = ["Java Servlets", "Cookies & Sessions", "JDBC", "MySQL / H2"];
  let cx = M;
  chips.forEach((c) => {
    const w = 0.28 + c.length * 0.115;
    s.addShape(pres.ShapeType.roundRect, { x: cx, y: 5.15, w, h: 0.44, rectRadius: 0.22, fill: { color: "2C5F2D" }, line: { color: MOSS, width: 1 } });
    s.addText(c, { x: cx, y: 5.15, w, h: 0.44, align: "center", valign: "middle", fontFace: SANS, fontSize: 12.5, color: MOSS_LT, isTextBox: true, margin: 0 });
    cx += w + 0.2;
  });
  s.addText("Presented by:  ______________________     \u00B7     Roll no: __________", {
    x: M, y: 6.5, w: 11, h: 0.4, fontFace: SANS, fontSize: 13, color: "7E9576", isTextBox: true, margin: 0,
  });
  s.addNotes("Introduce the project: a peer-to-peer campus resale marketplace built on Java Servlets, demonstrating cookies vs sessions, JDBC persistence, and an in-app wallet.");
})();

// =====================================================================
// SLIDE 2 — The problem / why now
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: CREAM };
  kicker(s, "The opportunity");
  title(s, "Campus resale is booming \u2014 but the tools are clunky");
  const items = [
    ["\u267B", "Circular economy is here", "Thrift, refurbished gadgets and campus swap markets are among the fastest-growing e-commerce categories going into 2026."],
    ["\uD83D\uDCDA", "Every hostel has the need", "Textbooks, calculators, lamps and furniture change hands every semester \u2014 usually over scattered WhatsApp groups."],
    ["\uD83D\uDEB6", "Guests get blocked", "Most marketplaces force you to sign up before you can even look. People leave before they browse."],
  ];
  const cw = (W - 2 * M - 2 * 0.4) / 3;
  items.forEach((it, i) => {
    const x = M + i * (cw + 0.4);
    card(s, x, 2.05, cw, 3.9, WHITE);
    iconCircle(s, x + 0.45, 2.5, 0.95, it[0], MOSS);
    s.addText(it[1], { x: x + 0.4, y: 3.65, w: cw - 0.8, h: 0.7, fontFace: SERIF, fontSize: 19, bold: true, color: FOREST, isTextBox: true, margin: 0 });
    s.addText(it[2], { x: x + 0.4, y: 4.35, w: cw - 0.8, h: 1.4, fontFace: SANS, fontSize: 14.5, color: SLATE, lineSpacingMultiple: 1.05, isTextBox: true, margin: 0 });
  });
  s.addText("Our answer: a campus-specific marketplace that lets you browse as a guest and pay from an in-app wallet.", {
    x: M, y: 6.25, w: W - 2 * M, h: 0.5, fontFace: SANS, fontSize: 15, italic: true, color: FOREST, isTextBox: true, margin: 0,
  });
})();

// =====================================================================
// SLIDE 3 — USP (dark)
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: FOREST };
  kicker(s, "Our unique selling point", M, 0.7, MOSS_LT);
  s.addText([
    { text: "Shop as a guest, keep your cart when you sign in, ", options: { color: WHITE } },
    { text: "and pay from a real in-app wallet", options: { color: AMBER } },
    { text: " \u2014 where reusing stuff earns you rewards.", options: { color: WHITE } },
  ], { x: M, y: 1.2, w: W - 2 * M, h: 2.3, fontFace: SERIF, fontSize: 33, bold: true, lineSpacingMultiple: 1.05, isTextBox: true, margin: 0 });

  const pill = [
    ["\uD83C\uDF6A", "Cookie \u2192 session", "Cart follows you from guest to logged-in \u2014 nothing lost."],
    ["\uD83D\uDCB0", "Wallet economy", "Real money moves from buyer to seller at checkout."],
    ["\uD83C\uDF31", "Green rewards", "Every item you rehome earns sustainability points."],
  ];
  const cw = (W - 2 * M - 2 * 0.4) / 3;
  pill.forEach((p, i) => {
    const x = M + i * (cw + 0.4);
    card(s, x, 3.95, cw, 2.65, "24521F");
    iconCircle(s, x + 0.4, 4.3, 0.85, p[0], MOSS_LT, FOREST_DK);
    s.addText(p[1], { x: x + 0.4, y: 5.3, w: cw - 0.8, h: 0.5, fontFace: SERIF, fontSize: 18, bold: true, color: WHITE, isTextBox: true, margin: 0 });
    s.addText(p[2], { x: x + 0.4, y: 5.8, w: cw - 0.8, h: 0.75, fontFace: SANS, fontSize: 13.5, color: MOSS_LT, lineSpacingMultiple: 1.03, isTextBox: true, margin: 0 });
  });
})();

// =====================================================================
// SLIDE 4 — Four modules grid
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: CREAM };
  kicker(s, "What it does");
  title(s, "Four core modules");
  const mods = [
    ["\uD83D\uDCE6", "Listings", "Create, edit and remove a resale listing with category, price and condition."],
    ["\uD83D\uDD0D", "Browse & Search", "Filter by text, category and max price. Open to guests, with a cookie-based \u201Crecently viewed\u201D strip."],
    ["\uD83D\uDED2", "Cart & Wallet", "A session cart and a checkout that deducts your in-app wallet balance."],
    ["\uD83D\uDCDC", "Transaction History", "Separate buyer (purchases) and seller (sales) views of every deal."],
  ];
  const cw = (W - 2 * M - 0.4) / 2, ch = 1.95;
  mods.forEach((m, i) => {
    const x = M + (i % 2) * (cw + 0.4);
    const y = 1.95 + Math.floor(i / 2) * (ch + 0.4);
    card(s, x, y, cw, ch, WHITE);
    iconCircle(s, x + 0.4, y + 0.45, 1.05, m[0], FOREST);
    s.addText(m[1], { x: x + 1.75, y: y + 0.35, w: cw - 2.1, h: 0.55, fontFace: SERIF, fontSize: 21, bold: true, color: FOREST, isTextBox: true, margin: 0 });
    s.addText(m[2], { x: x + 1.75, y: y + 0.9, w: cw - 2.1, h: 0.9, fontFace: SANS, fontSize: 14, color: SLATE, lineSpacingMultiple: 1.05, isTextBox: true, margin: 0 });
  });
})();

// =====================================================================
// SLIDE 5 — What makes ours different (standout features)
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: CREAM };
  kicker(s, "Standout features");
  title(s, "Why ours goes further");
  const rows = [
    ["\uD83C\uDF6A", "Guest cart \u2192 account migration", "Add items without logging in (saved in a cookie); the moment you sign in they move into your session. The exact cookies-vs-sessions concept, shown as a real benefit."],
    ["\uD83D\uDCB0", "A real wallet economy", "Checkout actually transfers money from the buyer\u2019s wallet to the seller\u2019s \u2014 atomically, all-or-nothing. Not a fake \u201Corder placed\u201D button."],
    ["\uD83C\uDF31", "Sustainability points", "Every rehomed item earns green points on your profile \u2014 a unique hook tied to the circular-economy theme."],
    ["\u26A1", "Zero-setup, one-click run", "Runs with a single command (embedded Tomcat + bundled database); switches to MySQL with one config line."],
    ["\uD83D\uDD12", "Real security touches", "SHA-256 password hashing, PreparedStatements (no SQL injection), server-side validation, session invalidation on logout."],
  ];
  let y = 1.95;
  const rh = 0.98;
  rows.forEach((r) => {
    card(s, M, y, W - 2 * M, rh - 0.14, WHITE);
    iconCircle(s, M + 0.28, y + 0.13, 0.6, r[0], MOSS);
    s.addText(r[1], { x: M + 1.1, y: y + 0.06, w: 3.9, h: rh - 0.2, fontFace: SERIF, fontSize: 16.5, bold: true, color: FOREST, valign: "middle", isTextBox: true, margin: 0 });
    s.addText(r[2], { x: M + 5.15, y: y + 0.06, w: W - 2 * M - 5.4, h: rh - 0.2, fontFace: SANS, fontSize: 13, color: SLATE, valign: "middle", lineSpacingMultiple: 1.0, isTextBox: true, margin: 0 });
    y += rh;
  });
})();

// =====================================================================
// SLIDE 6 — Cookies vs Sessions (core concept)
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: CREAM };
  kicker(s, "The core concept");
  title(s, "Cookies vs. Sessions \u2014 done right");

  // Left: cookies (guest)
  card(s, M, 2.1, 5.3, 4.0, WHITE);
  iconCircle(s, M + 0.4, 2.45, 0.85, "\uD83C\uDF6A", AMBER, WHITE);
  s.addText("Cookies  \u00B7  the guest", { x: M + 1.4, y: 2.55, w: 3.7, h: 0.6, fontFace: SERIF, fontSize: 20, bold: true, color: INK, valign: "middle", isTextBox: true, margin: 0 });
  s.addText([
    { text: "Guest cart  (guest_cart)", options: { bullet: true, breakLine: true } },
    { text: "Recently-viewed items  (recently_viewed)", options: { bullet: true, breakLine: true } },
    { text: "Stored in the browser \u2014 works with no login", options: { bullet: true, breakLine: true } },
    { text: "Survives across visits", options: { bullet: true, breakLine: false } },
  ], { x: M + 0.5, y: 3.55, w: 4.5, h: 2.3, fontFace: SANS, fontSize: 14.5, color: SLATE, paraSpaceAfter: 10, isTextBox: true, margin: 0 });

  // Right: session (member)
  const rx = W - M - 5.3;
  card(s, rx, 2.1, 5.3, 4.0, WHITE);
  iconCircle(s, rx + 0.4, 2.45, 0.85, "\uD83D\uDD10", FOREST, WHITE);
  s.addText("Session  \u00B7  the member", { x: rx + 1.4, y: 2.55, w: 3.7, h: 0.6, fontFace: SERIF, fontSize: 20, bold: true, color: INK, valign: "middle", isTextBox: true, margin: 0 });
  s.addText([
    { text: "Logged-in identity  (HttpSession)", options: { bullet: true, breakLine: true } },
    { text: "Cart in DB, keyed by session id", options: { bullet: true, breakLine: true } },
    { text: "Wallet & checkout \u2014 trusted, server-side", options: { bullet: true, breakLine: true } },
    { text: "Cleared on logout via session.invalidate()", options: { bullet: true, breakLine: false } },
  ], { x: rx + 0.5, y: 3.55, w: 4.5, h: 2.3, fontFace: SANS, fontSize: 14.5, color: SLATE, paraSpaceAfter: 10, isTextBox: true, margin: 0 });

  // Middle arrow
  s.addShape(pres.ShapeType.rightArrow, { x: 6.05, y: 3.75, w: 1.23, h: 0.7, fill: { color: FOREST }, line: { type: "none" } });
  s.addText("on login\nmigrate", { x: 5.75, y: 4.5, w: 1.8, h: 0.7, align: "center", fontFace: SANS, fontSize: 12, bold: true, color: FOREST, isTextBox: true, margin: 0 });
  s.addText("On login the cookie cart is merged into the session cart and the cookie is deleted \u2014 the visitor never loses their basket.", {
    x: M, y: 6.35, w: W - 2 * M, h: 0.5, fontFace: SANS, fontSize: 14, italic: true, color: FOREST, align: "center", isTextBox: true, margin: 0,
  });
})();

// =====================================================================
// SLIDE 7 — Architecture
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: CREAM };
  kicker(s, "How it's built");
  title(s, "Clean MVC architecture");
  const boxes = [
    ["\uD83C\uDF10", "Browser", "JSP + JSTL views\nBootstrap UI"],
    ["\u2699", "Servlets", "Controllers\n@WebServlet + Filter"],
    ["\uD83D\uDD0C", "Service / DAO", "Business logic\nPlain JDBC"],
    ["\uD83D\uDDC4", "Database", "H2 (default)\nor MySQL"],
  ];
  const bw = 2.7, bh = 2.5, gap = ((W - 2 * M) - 4 * bw) / 3;
  const y = 2.4;
  boxes.forEach((b, i) => {
    const x = M + i * (bw + gap);
    card(s, x, y, bw, bh, i === 3 ? FOREST : WHITE);
    const fg = i === 3 ? WHITE : FOREST;
    iconCircle(s, x + bw / 2 - 0.5, y + 0.35, 1.0, b[0], i === 3 ? MOSS_LT : MOSS, i === 3 ? FOREST_DK : WHITE);
    s.addText(b[1], { x, y: y + 1.35, w: bw, h: 0.5, align: "center", fontFace: SERIF, fontSize: 19, bold: true, color: fg, isTextBox: true, margin: 0 });
    s.addText(b[2], { x, y: y + 1.8, w: bw, h: 0.65, align: "center", fontFace: SANS, fontSize: 12.5, color: i === 3 ? MOSS_LT : SLATE, lineSpacingMultiple: 1.0, isTextBox: true, margin: 0 });
    if (i < 3) {
      s.addShape(pres.ShapeType.rightArrow, { x: x + bw + gap / 2 - 0.28, y: y + bh / 2 - 0.22, w: 0.56, h: 0.44, fill: { color: MOSS }, line: { type: "none" } });
    }
  });
  s.addText("Requests flow left \u2192 right; every database call uses PreparedStatements. The same code runs on H2 or MySQL unchanged.", {
    x: M, y: 5.55, w: W - 2 * M, h: 0.5, fontFace: SANS, fontSize: 14, italic: true, color: FOREST, align: "center", isTextBox: true, margin: 0,
  });
})();

// =====================================================================
// SLIDE 8 — Database schema
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: CREAM };
  kicker(s, "Data model");
  title(s, "Four tables, fully related");
  const tables = [
    ["students", ["student_id (PK)", "name, email", "password (SHA-256)", "wallet_balance", "sustainability_points"]],
    ["listings", ["listing_id (PK)", "seller_id \u2192 students", "title, category, price", "item_condition", "status (AVAILABLE/SOLD)"]],
    ["cart_items", ["cart_id (PK)", "session_id", "listing_id \u2192 listings", "added_at"]],
    ["transactions", ["txn_id (PK)", "buyer_id \u2192 students", "listing_id \u2192 listings", "amount, txn_date"]],
  ];
  const cw = (W - 2 * M - 3 * 0.35) / 4;
  tables.forEach((t, i) => {
    const x = M + i * (cw + 0.35);
    card(s, x, 2.0, cw, 4.05, WHITE);
    s.addShape(pres.ShapeType.roundRect, { x, y: 2.0, w: cw, h: 0.66, rectRadius: 0.12, fill: { color: FOREST }, line: { type: "none" } });
    s.addText(t[0], { x, y: 2.0, w: cw, h: 0.66, align: "center", valign: "middle", fontFace: SANS, fontSize: 15.5, bold: true, color: WHITE, isTextBox: true, margin: 0 });
    s.addText(t[1].map((f, j) => ({ text: f, options: { bullet: false, breakLine: j < t[1].length - 1 } })), {
      x: x + 0.28, y: 2.85, w: cw - 0.5, h: 3.0, fontFace: SANS, fontSize: 12.5, color: SLATE, paraSpaceAfter: 7, isTextBox: true, margin: 0,
    });
  });
  s.addText("A student has many listings and transactions; a listing has one seller and appears in carts and one sale.", {
    x: M, y: 6.35, w: W - 2 * M, h: 0.4, fontFace: SANS, fontSize: 13.5, italic: true, color: FOREST, align: "center", isTextBox: true, margin: 0,
  });
})();

// =====================================================================
// SLIDE 9 — Demo flow (list to purchase)
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: FOREST };
  kicker(s, "Live demo", M, 0.5, MOSS_LT);
  title(s, "A full list-to-purchase cycle", WHITE);
  const steps = [
    ["1", "Browse as guest", "Filter listings, add one to the cart \u2014 no login."],
    ["2", "Log in", "The guest cart migrates into your account."],
    ["3", "Review cart", "See items + your wallet balance."],
    ["4", "Checkout", "Wallet debited, seller credited."],
    ["5", "History", "Purchase appears under \u201Cbought.\u201D"],
    ["6", "Profile", "Wallet down, sustainability points up."],
  ];
  const cw = (W - 2 * M - 2 * 0.4) / 3, ch = 1.75;
  steps.forEach((st, i) => {
    const x = M + (i % 3) * (cw + 0.4);
    const y = 2.15 + Math.floor(i / 3) * (ch + 0.35);
    card(s, x, y, cw, ch, "24521F");
    iconCircle(s, x + 0.35, y + 0.35, 0.75, st[0], AMBER, FOREST_DK);
    s.addText(st[1], { x: x + 1.3, y: y + 0.32, w: cw - 1.6, h: 0.5, fontFace: SERIF, fontSize: 17, bold: true, color: WHITE, valign: "middle", isTextBox: true, margin: 0 });
    s.addText(st[2], { x: x + 1.3, y: y + 0.85, w: cw - 1.6, h: 0.7, fontFace: SANS, fontSize: 12.5, color: MOSS_LT, lineSpacingMultiple: 1.0, isTextBox: true, margin: 0 });
  });
  s.addNotes("Demo accounts: asha@campus.edu / rahul@campus.edu / neha@campus.edu, password 'password'. Start the app with run.cmd, open http://localhost:8080/.");
})();

// =====================================================================
// SLIDE 10 — Us vs typical projects
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: CREAM };
  kicker(s, "Where we win");
  title(s, "Us vs. a typical project");
  const cw = (W - 2 * M - 0.5) / 2;
  // typical
  card(s, M, 2.05, cw, 4.2, WHITE);
  s.addText("Typical student project", { x: M + 0.4, y: 2.3, w: cw - 0.8, h: 0.5, fontFace: SERIF, fontSize: 19, bold: true, color: SLATE, isTextBox: true, margin: 0 });
  s.addText([
    { text: "Forces login before you can browse", options: { bullet: { code: "2716" }, breakLine: true } },
    { text: "\u201COrder placed\u201D with no real payment", options: { bullet: { code: "2716" }, breakLine: true } },
    { text: "Login-only cart, lost if you leave", options: { bullet: { code: "2716" }, breakLine: true } },
    { text: "Needs full Tomcat + MySQL install to run", options: { bullet: { code: "2716" }, breakLine: true } },
    { text: "Plain-text passwords, string-built SQL", options: { bullet: { code: "2716" }, breakLine: false } },
  ], { x: M + 0.4, y: 2.95, w: cw - 0.8, h: 3.1, fontFace: SANS, fontSize: 15, color: SLATE, paraSpaceAfter: 12, isTextBox: true, margin: 0 });

  // ours
  const rx = M + cw + 0.5;
  card(s, rx, 2.05, cw, 4.2, FOREST);
  s.addText("Circular Campus Marketplace", { x: rx + 0.4, y: 2.3, w: cw - 0.8, h: 0.5, fontFace: SERIF, fontSize: 19, bold: true, color: WHITE, isTextBox: true, margin: 0 });
  s.addText([
    { text: "Full guest browsing + guest cart", options: { bullet: { code: "2714" }, breakLine: true } },
    { text: "Real wallet: money moves buyer \u2192 seller", options: { bullet: { code: "2714" }, breakLine: true } },
    { text: "Cart migrates cookie \u2192 session on login", options: { bullet: { code: "2714" }, breakLine: true } },
    { text: "One-click run; MySQL by one config line", options: { bullet: { code: "2714" }, breakLine: true } },
    { text: "Hashed passwords + PreparedStatements", options: { bullet: { code: "2714" }, breakLine: false } },
  ], { x: rx + 0.4, y: 2.95, w: cw - 0.8, h: 3.1, fontFace: SANS, fontSize: 15, color: MOSS_LT, paraSpaceAfter: 12, isTextBox: true, margin: 0 });
})();

// =====================================================================
// SLIDE 11 — Roadmap / future work
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: CREAM };
  kicker(s, "What's next");
  title(s, "Roadmap");
  const done = ["4 modules working end-to-end", "Cookie \u2192 session cart migration", "Wallet + atomic checkout", "Sustainability points"];
  const next = ["Item photos & image upload", "Ratings / reviews per sale", "Search pagination", "bcrypt hashing + CSRF tokens"];
  const cw = (W - 2 * M - 0.5) / 2;
  card(s, M, 2.05, cw, 4.0, WHITE);
  iconCircle(s, M + 0.4, 2.35, 0.7, "\u2714", MOSS);
  s.addText("Done", { x: M + 1.3, y: 2.4, w: 3, h: 0.6, fontFace: SERIF, fontSize: 20, bold: true, color: FOREST, valign: "middle", isTextBox: true, margin: 0 });
  s.addText(done.map((d, i) => ({ text: d, options: { bullet: { code: "2714" }, breakLine: i < done.length - 1 } })), {
    x: M + 0.5, y: 3.25, w: cw - 1, h: 2.6, fontFace: SANS, fontSize: 15.5, color: SLATE, paraSpaceAfter: 12, isTextBox: true, margin: 0,
  });
  const rx = M + cw + 0.5;
  card(s, rx, 2.05, cw, 4.0, WHITE);
  iconCircle(s, rx + 0.4, 2.35, 0.7, "\u2192", AMBER);
  s.addText("Next", { x: rx + 1.3, y: 2.4, w: 3, h: 0.6, fontFace: SERIF, fontSize: 20, bold: true, color: FOREST, valign: "middle", isTextBox: true, margin: 0 });
  s.addText(next.map((d, i) => ({ text: d, options: { bullet: { code: "2192" }, breakLine: i < next.length - 1 } })), {
    x: rx + 0.5, y: 3.25, w: cw - 1, h: 2.6, fontFace: SANS, fontSize: 15.5, color: SLATE, paraSpaceAfter: 12, isTextBox: true, margin: 0,
  });
})();

// =====================================================================
// SLIDE 12 — Closing
// =====================================================================
(() => {
  const s = pres.addSlide();
  s.background = { color: FOREST_DK };
  s.addText("\u267B", { x: -0.8, y: 3.2, w: 5, h: 5, fontSize: 260, color: "1F3F1B", align: "center", valign: "middle", isTextBox: true, margin: 0 });
  s.addText("Thank you", { x: M, y: 2.5, w: 11, h: 1.2, fontFace: SERIF, fontSize: 56, bold: true, color: WHITE, isTextBox: true, margin: 0 });
  s.addText("Circular Campus Marketplace  \u2014  Resale, Cart & Wallet", { x: M, y: 3.8, w: 11, h: 0.5, fontFace: SANS, fontSize: 18, color: MOSS_LT, isTextBox: true, margin: 0 });
  s.addText("Questions & live demo welcome.", { x: M, y: 4.35, w: 11, h: 0.5, fontFace: SANS, fontSize: 15, italic: true, color: "7E9576", isTextBox: true, margin: 0 });
  s.addText("Repo:  github.com/______________/circular-campus-marketplace", { x: M, y: 6.4, w: 11, h: 0.4, fontFace: SANS, fontSize: 13, color: "6E8767", isTextBox: true, margin: 0 });
})();

pres.writeFile({ fileName: "C:/Hero/WEDASS/docs/Circular-Campus-Marketplace.pptx" }).then((f) => {
  console.log("WROTE", f);
});
