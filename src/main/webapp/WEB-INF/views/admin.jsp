<%@ include file="_header.jsp" %>
<div class="section-head left"><h2>Marketplace moderation</h2><p>Review status, replace reference photos and record the moderation decision.</p></div>
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
