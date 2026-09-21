<%@ include file="_header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-3">
  <div><h2 class="mb-1">Notifications</h2><p class="text-muted mb-0">Waitlist and marketplace updates.</p></div>
  <form method="post" action="${ctx}/notifications"><input type="hidden" name="csrfToken" value="${csrfToken}"><button class="btn btn-outline-brand">Mark all read</button></form>
</div>
<c:choose><c:when test="${empty notifications}"><div class="surface pad">No notifications yet.</div></c:when><c:otherwise>
<div class="surface"><c:forEach var="n" items="${notifications}">
  <a class="notification-row ${n.read ? '' : 'unread'}" href="${ctx}${n.linkPath}"><div><c:out value="${n.message}"/></div><small><fmt:formatDate value="${n.createdAt}" pattern="dd MMM, HH:mm"/></small></a>
</c:forEach></div></c:otherwise></c:choose>
<%@ include file="_footer.jsp" %>
