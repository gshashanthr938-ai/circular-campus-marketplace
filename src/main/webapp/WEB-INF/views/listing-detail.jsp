<%@ include file="_header.jsp" %>

<a href="${ctx}/browse" class="text-muted small">&larr; Back to browse</a>

<div class="row g-4 mt-1">
    <div class="col-lg-6">
        <div class="detail-gallery">
            <c:forEach var="img" items="${listingImages}" varStatus="s"><img class="${s.first ? 'gallery-main' : ''}" src="${ctx}/img/${img}" alt="<c:out value='${listing.title}'/> picture ${s.count}"></c:forEach>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="d-flex align-items-center gap-2 mb-2">
            <span class="badge badge-cat"><c:out value="${listing.category}"/></span>
            <c:choose>
                <c:when test="${listing.available}"><span class="badge bg-success">Available</span></c:when>
                <c:otherwise><span class="badge badge-sold">Sold</span></c:otherwise>
            </c:choose>
            <span class="text-muted small">Condition: <c:out value="${listing.condition}"/></span>
            <c:if test="${listing.reviewCount > 0}"><span class="rating-pill">&#9733; <fmt:formatNumber value="${listing.averageRating}" maxFractionDigits="1"/> (${listing.reviewCount})</span></c:if>
        </div>
        <h1 style="font-size:32px;font-weight:800;"><c:out value="${listing.title}"/></h1>
        <div class="seller-trust-card">
            <div class="seller-avatar">&#128100;</div>
            <div class="seller-trust-main">
                <span class="trust-label">Student seller profile</span>
                <strong><c:out value="${listing.sellerName}"/></strong>
                <span class="trust-sub">Member since <fmt:formatDate value="${listing.sellerMemberSince}" pattern="MMM yyyy"/></span>
            </div>
            <div class="trust-stat"><strong>${listing.sellerSalesCount}</strong><span>completed sales</span></div>
            <div class="trust-stat">
                <strong><c:choose><c:when test="${listing.sellerReviewCount > 0}">&#9733; <fmt:formatNumber value="${listing.sellerAverageRating}" maxFractionDigits="1"/></c:when><c:otherwise>New</c:otherwise></c:choose></strong>
                <span>${listing.sellerReviewCount} seller reviews</span>
            </div>
            <div class="trust-contact"><span class="trust-shield">&#10003;</span><span><strong>Contact protected</strong><br>Email and phone are released to the buyer after payment.</span></div>
        </div>
        <p style="font-size:15.5px;color:var(--slate);"><c:out value="${listing.description}"/></p>
        <div class="price-tag my-3">&#8377;<fmt:formatNumber value="${listing.price}" minFractionDigits="2" maxFractionDigits="2"/></div>

        <c:choose>
            <c:when test="${ownListing}">
                <div class="alert alert-secondary mb-0">This is your listing.
                    <a href="${ctx}/sell?id=${listing.id}">Edit it</a>.</div>
            </c:when>
            <c:when test="${not listing.available}">
                <div class="alert alert-secondary">This item is not currently available. Checkout is first-come, first-served.</div>
                <c:if test="${not empty currentStudent and not waitlisted}"><form method="post" action="${ctx}/waitlist"><input type="hidden" name="csrfToken" value="${csrfToken}"><input type="hidden" name="id" value="${listing.id}"><button class="btn btn-primary">Notify me if available</button></form></c:if>
                <c:if test="${waitlisted}"><div class="text-muted">You are on this item's waitlist.</div></c:if>
            </c:when>
            <c:when test="${inCart}">
                <a class="btn btn-outline-brand btn-lg" href="${ctx}/cart">In your cart &rarr; Go to cart</a>
            </c:when>
            <c:otherwise>
                <form method="post" action="${ctx}/cart">
<input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="id" value="${listing.id}">
                    <input type="hidden" name="back" value="/listing?id=${listing.id}">
                    <button class="btn btn-primary btn-lg" type="submit">Add to cart</button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<section class="section pt-4">
  <div class="section-head left"><h2>Buyer reviews</h2><p>Only completed buyers can submit one review.</p></div>
  <c:choose><c:when test="${empty reviews}"><div class="surface pad text-muted">No reviews yet.</div></c:when><c:otherwise><div class="review-list">
    <c:forEach var="r" items="${reviews}"><article class="review-card"><div class="review-stars"><c:forEach begin="1" end="${r.rating}">&#9733;</c:forEach></div><strong><c:out value="${r.reviewerName}"/></strong><p><c:out value="${r.comment}"/></p><small><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy"/></small></article></c:forEach>
  </div></c:otherwise></c:choose>
</section>

<p class="text-muted small mt-3">Catalog photography is for reference; ask the seller for current item photos and confirm condition before pickup.</p>
<%@ include file="_footer.jsp" %>
