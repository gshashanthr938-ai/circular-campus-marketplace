<%@ include file="_header.jsp" %>

<div class="dashboard-heading"><div><h2 class="mb-1">Welcome back, <c:out value="${student.name}"/></h2><p class="text-muted">Your marketplace activity at a glance.</p></div><a class="btn btn-primary" href="${ctx}/sell">List an item</a></div>

<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="stat-box">
            <div class="text-muted">Name</div>
            <div class="num" style="font-size:1.2rem;"><c:out value="${student.name}"/></div>
            <div class="small text-muted"><c:out value="${student.email}"/></div>
            <div class="small text-muted"><c:out value="${student.phone}"/></div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-box">
            <div class="text-muted">Circular impact &#9851;</div>
            <div class="num">${student.sustainabilityPoints}</div>
            <div class="small text-muted">points &middot; about <fmt:formatNumber value="${estimatedCo2SavedKg}" maxFractionDigits="1"/> kg CO2e avoided</div>
        </div>
    </div>
    <div class="col-md-4"><div class="stat-box"><div class="text-muted">Accepted payments</div><div class="num" style="font-size:1.2rem;">UPI &amp; net banking</div><div class="small text-muted">Selected securely during checkout</div></div></div>
</div>

<div class="dashboard-grid mb-4">
  <a class="dashboard-link" href="${ctx}/my-listings"><strong>${listingCount}</strong><span>Listings</span></a>
  <a class="dashboard-link" href="${ctx}/history"><strong>${purchaseCount}</strong><span>Purchases</span></a>
  <a class="dashboard-link" href="${ctx}/history"><strong>${salesCount}</strong><span>Sales</span></a>
  <a class="dashboard-link" href="${ctx}/notifications"><strong>${notificationCount}</strong><span>Unread alerts</span></a>
</div>

<div class="surface pad mb-4"><div class="d-flex justify-content-between"><h5>Recent alerts</h5><a href="${ctx}/notifications">View all</a></div>
  <c:choose><c:when test="${empty notifications}"><p class="text-muted mb-0">No marketplace alerts yet.</p></c:when><c:otherwise><c:forEach var="n" items="${notifications}" end="2"><div class="mini-alert"><c:out value="${n.message}"/></div></c:forEach></c:otherwise></c:choose>
</div>

<div class="d-flex gap-2">
    <a class="btn btn-outline-secondary" href="${ctx}/history">View history</a>
    <a class="btn btn-outline-secondary" href="${ctx}/browse">Browse marketplace</a>
</div>

<%@ include file="_footer.jsp" %>
