<%@ include file="_header.jsp" %>

<h2 class="mb-3">My Profile</h2>

<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="stat-box">
            <div class="text-muted">Name</div>
            <div class="num" style="font-size:1.2rem;"><c:out value="${student.name}"/></div>
            <div class="small text-muted"><c:out value="${student.email}"/></div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-box">
            <div class="text-muted">Wallet balance</div>
            <div class="num">&#8377;<fmt:formatNumber value="${student.walletBalance}" minFractionDigits="2" maxFractionDigits="2"/></div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-box">
            <div class="text-muted">Sustainability points &#9851;</div>
            <div class="num">${student.sustainabilityPoints}</div>
            <div class="small text-muted">+10 for every item you rehome</div>
        </div>
    </div>
</div>

<div class="d-flex gap-2">
    <a class="btn btn-brand" href="${ctx}/sell">List an item</a>
    <a class="btn btn-outline-secondary" href="${ctx}/history">View history</a>
</div>

<%@ include file="_footer.jsp" %>
