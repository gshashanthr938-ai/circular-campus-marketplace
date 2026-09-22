<%@ include file="_header.jsp" %>
<div class="section-head left"><h2>Marketplace control centre</h2><p>Monitor campus resale activity, sustainability impact and listing safety.</p></div>

<div class="admin-kpi-grid mb-4">
  <div class="admin-kpi"><span>Registered students</span><strong>${analytics.students}</strong><small>trusted marketplace accounts</small></div>
  <div class="admin-kpi"><span>Active inventory</span><strong>${analytics.availableListings}</strong><small>of ${analytics.listings} total listings</small></div>
  <div class="admin-kpi"><span>Completed resales</span><strong>${analytics.completedTransactions}</strong><small>INR <fmt:formatNumber value="${analytics.transactionValue}" maxFractionDigits="0"/> circulated</small></div>
  <div class="admin-kpi impact"><span>Estimated CO2e avoided</span><strong><fmt:formatNumber value="${analytics.estimatedCo2SavedKg}" maxFractionDigits="1"/> kg</strong><small>2.5 kg estimate per resale</small></div>
</div>

<div class="admin-insight-grid mb-5">
  <section class="surface pad">
    <div class="insight-head"><div><h5>Inventory by category</h5><p>Current supply across the marketplace</p></div><span>${analytics.listings} items</span></div>
    <c:forEach var="row" items="${analytics.categories}">
      <div class="metric-row"><div><span><c:out value="${row.label}"/></span><strong>${row.count}</strong></div><div class="metric-track"><i style="width:${row.percentage}%"></i></div></div>
    </c:forEach>
  </section>
  <section class="surface pad">
    <div class="insight-head"><div><h5>Trust and checkout</h5><p>Signals that help administrators assess quality</p></div><span>${analytics.waitlistedStudents} waiting</span></div>
    <div class="trust-summary"><div><strong><c:choose><c:when test="${analytics.reviewCount > 0}"><fmt:formatNumber value="${analytics.averageRating}" maxFractionDigits="1"/>/5</c:when><c:otherwise>New</c:otherwise></c:choose></strong><span>${analytics.reviewCount} verified reviews</span></div><div><strong>${analytics.completedTransactions}</strong><span>atomic checkouts</span></div></div>
    <c:choose><c:when test="${empty analytics.paymentMethods}"><p class="text-muted small mb-0">Payment-method trends appear after the first checkout.</p></c:when><c:otherwise><c:forEach var="row" items="${analytics.paymentMethods}"><div class="payment-stat"><span><c:out value="${row.label}"/></span><strong>${row.count}</strong></div></c:forEach></c:otherwise></c:choose>
  </section>
</div>

<div class="section-head left"><h3>Listing moderation</h3><p>Review status, replace reference photos and record the moderation decision.</p></div>
<div class="admin-list"><c:forEach var="l" items="${listings}"><article class="admin-card">
  <img src="${ctx}/img/${l.image}" alt=""><div class="admin-card-body">
    <div class="d-flex justify-content-between gap-2"><strong><c:out value="${l.title}"/></strong><span class="cat-chip"><c:out value="${l.status}"/></span></div>
    <small>Seller: <c:out value="${l.sellerName}"/> &middot; INR <fmt:formatNumber value="${l.price}" maxFractionDigits="0"/></small>
    <form method="post" action="${ctx}/admin" class="admin-form mt-3"><input type="hidden" name="csrfToken" value="${csrfToken}"><input type="hidden" name="id" value="${l.id}">
      <label class="form-label">Listing photo</label><select name="imagePath" class="form-select"><c:forEach var="img" items="${imageOptions}"><option value="${img}" ${img == l.image ? 'selected' : ''}><c:out value="${img}"/></option></c:forEach></select>
      <label class="form-label mt-2">Status</label><select name="status" class="form-select"><option ${l.status == 'AVAILABLE' ? 'selected' : ''}>AVAILABLE</option><option ${l.status == 'SOLD' ? 'selected' : ''}>SOLD</option><option ${l.status == 'REMOVED' ? 'selected' : ''}>REMOVED</option></select>
      <label class="form-label mt-2">Moderation note</label><input name="note" maxlength="500" class="form-control" value="<c:out value='${l.moderationNote}'/>">
      <button class="btn btn-primary mt-3">Save moderation</button>
    </form>
  </div></article></c:forEach></div>
<%@ include file="_footer.jsp" %>
