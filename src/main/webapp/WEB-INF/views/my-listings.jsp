<%@ include file="_header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0">My Listings</h2>
    <a class="btn btn-brand" href="${ctx}/sell">+ New listing</a>
</div>

<c:choose>
    <c:when test="${empty listings}">
        <div class="alert alert-light border">You haven't listed anything yet.
            <a href="${ctx}/sell">List your first item</a>.</div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table align-middle bg-white">
                <thead>
                <tr>
                    <th>Item</th>
                    <th>Category</th>
                    <th class="text-end">Price</th>
                    <th>Status</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="l" items="${listings}">
                    <tr>
                        <td><a href="${ctx}/listing?id=${l.id}"><c:out value="${l.title}"/></a></td>
                        <td><span class="badge badge-cat"><c:out value="${l.category}"/></span></td>
                        <td class="text-end">&#8377;<fmt:formatNumber value="${l.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${l.available}"><span class="badge bg-success">Available</span></c:when>
                                <c:otherwise><span class="badge badge-sold"><c:out value="${l.status}"/></span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <c:if test="${l.available}">
                                <a class="btn btn-sm btn-outline-secondary" href="${ctx}/sell?id=${l.id}">Edit</a>
                                <form method="post" action="${ctx}/my-listings" class="d-inline"
                                      onsubmit="return confirm('Remove this listing?');">
<input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${l.id}">
                                    <button class="btn btn-sm btn-outline-danger" type="submit">Delete</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="_footer.jsp" %>
