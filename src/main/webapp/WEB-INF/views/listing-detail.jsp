<%@ include file="_header.jsp" %>

<a href="${ctx}/browse" class="text-muted small">&larr; Back to browse</a>

<div class="row g-4 mt-1">
    <div class="col-lg-6">
        <div class="detail-img">
            <img src="${ctx}/img/${listing.image}" alt="<c:out value='${listing.title}'/>">
        </div>
    </div>
    <div class="col-lg-6">
        <div class="d-flex align-items-center gap-2 mb-2">
            <span class="badge badge-cat">${listing.category}</span>
            <c:choose>
                <c:when test="${listing.available}"><span class="badge bg-success">Available</span></c:when>
                <c:otherwise><span class="badge badge-sold">Sold</span></c:otherwise>
            </c:choose>
            <span class="text-muted small">Condition: ${listing.condition}</span>
        </div>
        <h1 style="font-size:32px;font-weight:800;"><c:out value="${listing.title}"/></h1>
        <p class="text-muted">Sold by <strong><c:out value="${listing.sellerName}"/></strong></p>
        <p style="font-size:15.5px;color:var(--slate);"><c:out value="${listing.description}"/></p>
        <div class="price-tag my-3">&#8377;<fmt:formatNumber value="${listing.price}" minFractionDigits="2" maxFractionDigits="2"/></div>

        <c:choose>
            <c:when test="${ownListing}">
                <div class="alert alert-secondary mb-0">This is your listing.
                    <a href="${ctx}/sell?id=${listing.id}">Edit it</a>.</div>
            </c:when>
            <c:when test="${not listing.available}">
                <div class="alert alert-secondary mb-0">This item has already been sold.</div>
            </c:when>
            <c:when test="${inCart}">
                <a class="btn btn-outline-brand btn-lg" href="${ctx}/cart">In your cart &rarr; Go to cart</a>
            </c:when>
            <c:otherwise>
                <form method="post" action="${ctx}/cart">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="id" value="${listing.id}">
                    <input type="hidden" name="back" value="/listing?id=${listing.id}">
                    <button class="btn btn-primary btn-lg" type="submit">Add to cart</button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="_footer.jsp" %>
