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
                    <thead><tr><th>Item</th><th>Seller and pickup contact</th><th class="text-end">Paid</th><th>Payment</th><th>Review</th></tr></thead>
                    <tbody>
                    <c:forEach var="t" items="${purchases}">
                        <tr>
                            <td><c:out value="${t.listingTitle}"/></td>
                            <td>
                                <div class="contact-unlocked"><span>&#10003;</span> Contact unlocked</div>
                                <strong><c:out value="${t.counterpartyName}"/></strong>
                                <div class="pickup-contact"><span>&#9993;</span> <c:out value="${t.counterpartyEmail}"/></div>
                                <div class="pickup-contact"><span>&#9742;</span> <c:out value="${empty t.counterpartyPhone ? 'Not provided' : t.counterpartyPhone}"/></div>
                            </td>
                            <td class="text-end">&#8377;<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            <td class="small text-muted"><fmt:formatDate value="${t.txnDate}" pattern="dd MMM, HH:mm"/><br><c:out value="${t.paymentMethod}"/> &middot; <c:out value="${t.paymentReference}"/><br><span class="text-success">Terms accepted</span></td>
                            <td><c:choose><c:when test="${t.reviewed}"><span class="text-muted small">Reviewed</span></c:when><c:otherwise>
                              <form method="post" action="${ctx}/review" class="review-form"><input type="hidden" name="csrfToken" value="${csrfToken}"><input type="hidden" name="txnId" value="${t.txnId}"><select name="rating" class="form-select form-select-sm" required><option value="">Stars</option><option value="5">5</option><option value="4">4</option><option value="3">3</option><option value="2">2</option><option value="1">1</option></select><input name="comment" class="form-control form-control-sm" maxlength="800" placeholder="How was the item?" required><button class="btn btn-sm btn-primary">Post</button></form>
                            </c:otherwise></c:choose></td>
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
                    <thead><tr><th>Item</th><th>Buyer contact</th><th class="text-end">Received</th><th>Payment</th></tr></thead>
                    <tbody>
                    <c:forEach var="t" items="${sales}">
                        <tr>
                            <td><c:out value="${t.listingTitle}"/></td>
                            <td><strong><c:out value="${t.counterpartyName}"/></strong><div class="pickup-contact"><c:out value="${t.counterpartyEmail}"/></div><div class="pickup-contact"><c:out value="${empty t.counterpartyPhone ? 'Not provided' : t.counterpartyPhone}"/></div></td>
                            <td class="text-end">&#8377;<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            <td class="small text-muted"><fmt:formatDate value="${t.txnDate}" pattern="dd MMM, HH:mm"/><br><c:out value="${t.paymentMethod}"/> &middot; <c:out value="${t.paymentReference}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="_footer.jsp" %>
