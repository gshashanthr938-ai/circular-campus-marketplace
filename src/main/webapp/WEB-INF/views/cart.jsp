<%@ include file="_header.jsp" %>

<h1 style="font-size:30px;font-weight:800;" class="mb-3">Your Cart</h1>

<c:choose>
    <c:when test="${empty items}">
        <div class="surface pad text-center">
            <p class="mb-3 text-muted">Your cart is empty.</p>
            <a class="btn btn-primary" href="${ctx}/browse">Browse listings</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-4">
            <div class="col-lg-8">
                <div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead>
                        <tr><th colspan="2">Item</th><th>Seller</th><th class="text-end">Price</th><th></th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="l" items="${items}">
                            <tr>
                                <td style="width:72px;">
                                    <img src="${ctx}/img/${l.image}" alt="" width="60" height="46"
                                         style="object-fit:cover;border-radius:10px;border:1px solid var(--line);">
                                </td>
                                <td>
                                    <a class="fw-semibold text-dark" href="${ctx}/listing?id=${l.id}"><c:out value="${l.title}"/></a>
                                    <div><span class="cat-chip">${l.category}</span></div>
                                </td>
                                <td class="text-muted"><c:out value="${l.sellerName}"/></td>
                                <td class="text-end fw-bold">&#8377;<fmt:formatNumber value="${l.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td class="text-end">
                                    <form method="post" action="${ctx}/cart">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="id" value="${l.id}">
                                        <input type="hidden" name="back" value="/cart">
                                        <button class="btn btn-sm btn-outline-danger" type="submit">Remove</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="col-lg-4">
                <div class="surface pad">
                    <h5 class="mb-3">Order summary</h5>
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Items</span><span class="fw-semibold">${items.size()}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-3">
                        <span class="text-muted">Total</span>
                        <span class="price">&#8377;<fmt:formatNumber value="${total}" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                    <hr>
                    <c:choose>
                        <c:when test="${empty currentStudent}">
                            <div class="alert alert-warning">
                                This is a <strong>guest cart</strong> (saved in a cookie). Log in to check out &mdash;
                                your items move into your account automatically.
                            </div>
                            <a class="btn btn-primary w-100" href="${ctx}/login">Log in to checkout</a>
                        </c:when>
                        <c:otherwise>
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">Wallet</span>
                                <span class="wallet-pill">&#8377;<fmt:formatNumber value="${wallet}" minFractionDigits="2" maxFractionDigits="2"/></span>
                            </div>
                            <c:choose>
                                <c:when test="${affordable}">
                                    <form method="post" action="${ctx}/checkout">
                                        <button class="btn btn-primary btn-lg w-100" type="submit">Checkout &amp; pay from wallet</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-danger mb-0">Not enough wallet balance for this cart.</div>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="_footer.jsp" %>
