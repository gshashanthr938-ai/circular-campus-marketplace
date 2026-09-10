<%@ include file="_header.jsp" %>

<h2 class="mb-3">Transaction History</h2>

<div class="row g-4">
    <div class="col-lg-6">
        <h5>Items I bought</h5>
        <c:choose>
            <c:when test="${empty purchases}">
                <div class="alert alert-light border">No purchases yet.</div>
            </c:when>
            <c:otherwise>
                <table class="table bg-white">
                    <thead><tr><th>Item</th><th>Seller</th><th class="text-end">Paid</th><th>Date</th></tr></thead>
                    <tbody>
                    <c:forEach var="t" items="${purchases}">
                        <tr>
                            <td><c:out value="${t.listingTitle}"/></td>
                            <td><c:out value="${t.counterpartyName}"/></td>
                            <td class="text-end">&#8377;<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            <td class="small text-muted"><fmt:formatDate value="${t.txnDate}" pattern="dd MMM, HH:mm"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="col-lg-6">
        <h5>Items I sold</h5>
        <c:choose>
            <c:when test="${empty sales}">
                <div class="alert alert-light border">No sales yet.</div>
            </c:when>
            <c:otherwise>
                <table class="table bg-white">
                    <thead><tr><th>Item</th><th>Buyer</th><th class="text-end">Received</th><th>Date</th></tr></thead>
                    <tbody>
                    <c:forEach var="t" items="${sales}">
                        <tr>
                            <td><c:out value="${t.listingTitle}"/></td>
                            <td><c:out value="${t.counterpartyName}"/></td>
                            <td class="text-end">&#8377;<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            <td class="small text-muted"><fmt:formatDate value="${t.txnDate}" pattern="dd MMM, HH:mm"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="_footer.jsp" %>
