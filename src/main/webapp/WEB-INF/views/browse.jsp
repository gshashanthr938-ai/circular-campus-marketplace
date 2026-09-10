<%@ include file="_header.jsp" %>

<!-- ===== HERO ===== -->
<section class="hero">
    <div class="hero-copy">
        <span class="hero-eyebrow">&#128081; Student-to-student marketplace</span>
        <h1>Everything students need,<br><span class="grad">in one campus place.</span></h1>
        <p>Buy, sell and discover second-hand books, gadgets and hostel essentials from students
           around you &mdash; and pay instantly from your in-app wallet.</p>
        <div class="hero-cta">
            <a class="btn btn-primary btn-lg" href="#listings">Browse items</a>
            <a class="btn btn-accent btn-lg" href="${ctx}/sell">Sell an item</a>
            <c:if test="${empty currentStudent}">
                <a class="btn btn-ghost btn-lg" href="${ctx}/register">Join free</a>
            </c:if>
        </div>
    </div>
    <div class="hero-art">
        <img src="${ctx}/img/hero.svg" alt="Campus marketplace illustration">
    </div>
</section>

<!-- ===== CATEGORIES ===== -->
<section class="section">
    <div class="section-head">
        <h2>Browse categories</h2>
        <p>Jump straight to what you need this semester</p>
    </div>
    <div class="cat-grid">
        <a class="cat-tile" href="${ctx}/browse?category=Books">
            <div class="cat-ico"><img src="${ctx}/img/ic-books.svg" alt=""></div>
            <h5>Books</h5><p>Textbooks, novels & study material</p>
        </a>
        <a class="cat-tile" href="${ctx}/browse?category=Electronics">
            <div class="cat-ico"><img src="${ctx}/img/ic-electronics.svg" alt=""></div>
            <h5>Electronics</h5><p>Calculators, gadgets & devices</p>
        </a>
        <a class="cat-tile" href="${ctx}/browse?category=Furniture">
            <div class="cat-ico"><img src="${ctx}/img/ic-furniture.svg" alt=""></div>
            <h5>Furniture</h5><p>Tables, chairs & storage</p>
        </a>
        <a class="cat-tile" href="${ctx}/browse?category=Hostel Essentials">
            <div class="cat-ico"><img src="${ctx}/img/ic-hostel.svg" alt=""></div>
            <h5>Hostel Essentials</h5><p>Lamps, mugs & daily needs</p>
        </a>
    </div>
</section>

<!-- ===== RECENTLY VIEWED (cookie) ===== -->
<c:if test="${not empty recentlyViewed}">
    <section class="section">
        <div class="recent-strip">
            <div class="rv-title">Recently viewed &middot; remembered on this device (cookie)</div>
            <c:forEach var="r" items="${recentlyViewed}">
                <a class="chip-link" href="${ctx}/listing?id=${r.id}"><c:out value="${r.title}"/></a>
            </c:forEach>
        </div>
    </section>
</c:if>

<!-- ===== FILTER + LISTINGS ===== -->
<section class="section" id="listings">
    <div class="section-head left">
        <h2>Featured listings</h2>
        <p>Recently listed items from students on campus</p>
    </div>

    <form class="filter-bar row g-2 align-items-end mb-4" method="get" action="${ctx}/browse">
        <div class="col-md-5">
            <label class="form-label">Search</label>
            <input type="text" name="search" class="form-control" placeholder="e.g. calculator, textbook"
                   value="<c:out value='${search}'/>">
        </div>
        <div class="col-md-3">
            <label class="form-label">Category</label>
            <select name="category" class="form-select">
                <option value="">All categories</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat}" ${cat == selectedCategory ? 'selected' : ''}>${cat}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label">Max price (&#8377;)</label>
            <input type="number" step="0.01" min="0" name="maxPrice" class="form-control"
                   value="<c:out value='${maxPrice}'/>">
        </div>
        <div class="col-md-2 d-grid">
            <button class="btn btn-primary" type="submit">Filter</button>
        </div>
    </form>

    <c:choose>
        <c:when test="${empty listings}">
            <div class="alert alert-light text-center">No listings match your filters yet.</div>
        </c:when>
        <c:otherwise>
            <div class="product-grid">
                <c:forEach var="l" items="${listings}">
                    <div class="product-card">
                        <div class="product-img">
                            <img src="${ctx}/img/${l.image}" alt="<c:out value='${l.title}'/>">
                            <span class="cond-badge cond-${l.conditionClass}">${l.condition}</span>
                            <span class="wish">
                                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 1 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8z"/>
                                </svg>
                            </span>
                        </div>
                        <div class="product-body">
                            <span class="cat-chip">${l.category}</span>
                            <a class="product-title" href="${ctx}/listing?id=${l.id}"><c:out value="${l.title}"/></a>
                            <div class="seller">by <c:out value="${l.sellerName}"/></div>
                            <div class="price-row">
                                <span class="price">&#8377;<fmt:formatNumber value="${l.price}" maxFractionDigits="0"/></span>
                                <form method="post" action="${ctx}/cart">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="id" value="${l.id}">
                                    <input type="hidden" name="back" value="/browse#listings">
                                    <button class="add-btn" type="submit" title="Add to cart">+</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<!-- ===== ABOUT US ===== -->
<section class="section">
    <div class="section-head">
        <h2>Why CampusMarket?</h2>
        <p>Built for how students actually buy and sell during the semester</p>
    </div>
    <div class="feature-grid">
        <div class="feature">
            <div class="fico"><img src="${ctx}/img/ic-cart.svg" alt=""></div>
            <h4>Buy &amp; sell in seconds</h4>
            <p>List an item with a price and condition in under a minute, or grab what you need with
               a single tap &mdash; even before you sign in, thanks to a guest cart.</p>
        </div>
        <div class="feature">
            <div class="fico"><img src="${ctx}/img/ic-shield.svg" alt=""></div>
            <h4>A trusted campus network</h4>
            <p>Everyone is a verified student account. Deals happen within your own campus community,
               so pickups are simple and safe.</p>
        </div>
        <div class="feature">
            <div class="fico"><img src="${ctx}/img/ic-wallet.svg" alt=""></div>
            <h4>Instant in-app wallet</h4>
            <p>Skip the cash. Pay from your wallet at checkout and money moves straight to the seller
               &mdash; every purchase recorded in your history.</p>
        </div>
    </div>
</section>

<!-- ===== HOW IT WORKS ===== -->
<section class="section">
    <div class="section-head">
        <h2>How CampusMarket works</h2>
        <p>From listing to purchase in four simple steps</p>
    </div>
    <div class="steps">
        <div class="step">
            <div class="num">1</div>
            <h5>List or browse</h5>
            <p>Post an item, or filter listings by category and price &mdash; open to guests.</p>
        </div>
        <div class="step">
            <div class="num">2</div>
            <h5>Add to cart</h5>
            <p>Build a cart as a guest; it follows you into your account the moment you log in.</p>
        </div>
        <div class="step">
            <div class="num">3</div>
            <h5>Checkout from wallet</h5>
            <p>Pay instantly &mdash; your wallet is debited and the seller is credited.</p>
        </div>
        <div class="step">
            <div class="num">4</div>
            <h5>Track &amp; earn</h5>
            <p>See every deal in your history and earn sustainability points for reusing.</p>
        </div>
    </div>
</section>

<%@ include file="_footer.jsp" %>
