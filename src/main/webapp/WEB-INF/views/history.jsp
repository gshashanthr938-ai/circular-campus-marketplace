<%@ include file="_header.jsp" %>

<div class="section-head left history-heading">
    <div><span class="eyebrow">Protected orders</span><h2>Orders, pickups &amp; refunds</h2><p>Pay, inspect and confirm&mdash;or cancel safely before pickup.</p></div>
    <div class="handover-legend"><span>1</span> Pay <i></i><span>2</span> Meet &amp; inspect <i></i><span>3</span> Confirm</div>
</div>

<div class="refund-safety-note">
    <strong>If something changes:</strong>
    <span>An interrupted checkout rolls back without creating a completed order. After payment and before pickup, choose a refund to the original UPI/bank source or your CampusMarket wallet.</span>
    <a href="${ctx}/wallet">Open wallet</a>
</div>

<div class="history-grid">
    <section>
        <h4 class="history-title">My purchases <span>${purchases.size()}</span></h4>
        <c:choose>
            <c:when test="${empty purchases}"><div class="surface pad text-muted">No purchases yet. Your pickup and refund options will appear here.</div></c:when>
            <c:otherwise><div class="order-stack">
                <c:forEach var="t" items="${purchases}">
                    <article class="order-card ${t.refunded ? 'refunded-order' : ''}" data-txn-id="${t.txnId}">
                        <div class="order-top">
                            <div><small>Order #${t.txnId}</small><h5><a href="${ctx}/listing?id=${t.listingId}"><c:out value="${t.listingTitle}"/></a></h5></div>
                            <c:choose><c:when test="${t.refunded}"><span class="fulfillment-pill refunded">Refunded</span></c:when><c:when test="${t.pickupCompleted}"><span class="fulfillment-pill complete">Pickup completed</span></c:when><c:otherwise><span class="fulfillment-pill waiting">Awaiting pickup</span></c:otherwise></c:choose>
                        </div>
                        <div class="order-money"><strong>&#8377;<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></strong><span><c:out value="${t.paymentMethod}"/> &middot; <c:out value="${t.paymentReference}"/></span><small><fmt:formatDate value="${t.txnDate}" pattern="dd MMM yyyy, HH:mm"/> &middot; Terms accepted</small></div>

                        <c:choose>
                            <c:when test="${t.refunded}">
                                <div class="refund-complete"><span>&#8634;</span><div><strong>${t.refundedToWallet ? 'Returned to CampusMarket wallet' : 'Returned to original payment source'}</strong><small><fmt:formatDate value="${t.refundedAt}" pattern="dd MMM yyyy, HH:mm"/> &middot; <c:out value="${t.refundReference}"/></small><p><c:out value="${t.refundReason}"/></p></div></div>
                                <c:if test="${t.refundedToWallet}"><a class="btn btn-outline-brand btn-sm mt-2" href="${ctx}/wallet">View or withdraw wallet balance</a></c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="contact-panel"><div><span class="contact-unlocked">&#10003; Contact unlocked</span><strong><c:out value="${t.counterpartyName}"/></strong></div><div><span>&#9993; <c:out value="${t.counterpartyEmail}"/></span><span>&#9742; <c:out value="${empty t.counterpartyPhone ? 'Not provided' : t.counterpartyPhone}"/></span></div></div>
                                <c:choose>
                                    <c:when test="${t.pickupCompleted}"><div class="pickup-complete"><span>&#10003;</span><div><strong>Handover verified</strong><small>Completed <fmt:formatDate value="${t.pickupCompletedAt}" pattern="dd MMM yyyy, HH:mm"/></small></div></div></c:when>
                                    <c:otherwise>
                                        <div class="pickup-flow"><div><b>1</b><span><strong>Meet safely</strong><small>Choose a public campus location.</small></span></div><div><b>2</b><span><strong>Inspect the item</strong><small>Check condition, photos and accessories.</small></span></div><div><b>3</b><span><strong>Share this code</strong><small>Only after you receive the item.</small></span></div></div>
                                        <div class="pickup-code-wrap"><span>Your private pickup code</span><strong class="pickup-code" data-handover-code="${t.handoverCode}"><c:out value="${t.handoverCode}"/></strong><small>The seller enters this code to confirm delivery. Keep it private until inspection.</small></div>
                                        <details class="refund-panel"><summary>Need to cancel this order?</summary><div class="refund-panel-body"><p>Available only before the seller confirms pickup. The cancellation, listing release and refund happen together.</p><form method="post" action="${ctx}/refund"><input type="hidden" name="csrfToken" value="${csrfToken}"><input type="hidden" name="txnId" value="${t.txnId}"><div class="refund-destinations"><label><input type="radio" name="refundMethod" value="ORIGINAL_METHOD" checked><span><strong>Original payment source</strong><small>Return to the same UPI or net-banking source.</small></span></label><label><input type="radio" name="refundMethod" value="WALLET"><span><strong>CampusMarket wallet</strong><small>Instant wallet credit; withdraw later through UPI or net banking.</small></span></label></div><label class="form-label mt-2">Reason</label><textarea name="reason" class="form-control" minlength="5" maxlength="500" rows="2" placeholder="For example: seller cannot complete the pickup" required></textarea><button class="btn btn-refund btn-sm mt-2">Confirm cancellation &amp; refund</button><small class="refund-simulation">Academic simulation: original-source refunds record a reference but do not contact a real bank.</small></form></div></details>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>

                        <div class="review-zone"><c:choose><c:when test="${t.refunded}"><span class="review-locked">Order cancelled &mdash; review unavailable</span></c:when><c:when test="${t.reviewed}"><span class="verified-review">&#9733; Verified review submitted</span></c:when><c:when test="${not t.pickupCompleted}"><span class="review-locked">Review unlocks after pickup confirmation</span></c:when><c:otherwise><form method="post" action="${ctx}/review" class="review-form"><input type="hidden" name="csrfToken" value="${csrfToken}"><input type="hidden" name="txnId" value="${t.txnId}"><select name="rating" class="form-select form-select-sm" required><option value="">Stars</option><option value="5">5</option><option value="4">4</option><option value="3">3</option><option value="2">2</option><option value="1">1</option></select><input name="comment" class="form-control form-control-sm" maxlength="800" placeholder="How was the item and seller?" required><button class="btn btn-sm btn-primary">Post review</button></form></c:otherwise></c:choose></div>
                    </article>
                </c:forEach>
            </div></c:otherwise>
        </c:choose>
    </section>

    <section>
        <h4 class="history-title">My sales <span>${sales.size()}</span></h4>
        <c:choose>
            <c:when test="${empty sales}"><div class="surface pad text-muted">No sales yet. Buyer contact and handover confirmation will appear here.</div></c:when>
            <c:otherwise><div class="order-stack">
                <c:forEach var="t" items="${sales}">
                    <article class="order-card seller-order ${t.refunded ? 'refunded-order' : ''}" data-txn-id="${t.txnId}">
                        <div class="order-top"><div><small>Sale #${t.txnId}</small><h5><a href="${ctx}/listing?id=${t.listingId}"><c:out value="${t.listingTitle}"/></a></h5></div><c:choose><c:when test="${t.refunded}"><span class="fulfillment-pill refunded">Cancelled</span></c:when><c:when test="${t.pickupCompleted}"><span class="fulfillment-pill complete">Delivered</span></c:when><c:otherwise><span class="fulfillment-pill waiting">Pickup pending</span></c:otherwise></c:choose></div>
                        <div class="order-money"><strong>&#8377;<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></strong><span>${t.refunded ? 'Payment refunded' : 'Payment confirmed'} by <c:out value="${t.paymentMethod}"/></span><small><c:out value="${t.refunded ? t.refundReference : t.paymentReference}"/></small></div>
                        <c:choose>
                            <c:when test="${t.refunded}"><div class="refund-complete seller-refund"><span>&#8634;</span><div><strong>Order cancelled and listing reopened</strong><small>The buyer selected ${t.refundedToWallet ? 'wallet credit' : 'the original payment source'}.</small><p><c:out value="${t.refundReason}"/></p></div></div></c:when>
                            <c:otherwise>
                                <div class="contact-panel"><div><span class="contact-unlocked">Buyer contact</span><strong><c:out value="${t.counterpartyName}"/></strong></div><div><span>&#9993; <c:out value="${t.counterpartyEmail}"/></span><span>&#9742; <c:out value="${empty t.counterpartyPhone ? 'Not provided' : t.counterpartyPhone}"/></span></div></div>
                                <c:choose><c:when test="${t.pickupCompleted}"><div class="pickup-complete"><span>&#10003;</span><div><strong>Delivery confirmed</strong><small><fmt:formatDate value="${t.pickupCompletedAt}" pattern="dd MMM yyyy, HH:mm"/></small></div></div></c:when><c:otherwise><div class="seller-handover"><div><strong>Confirm the handover</strong><p>Meet the buyer, let them inspect the item, then ask for their private six-digit code.</p></div><form method="post" action="${ctx}/handover" class="handover-form"><input type="hidden" name="csrfToken" value="${csrfToken}"><input type="hidden" name="txnId" value="${t.txnId}"><input name="handoverCode" class="form-control" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" placeholder="6-digit code" aria-label="Buyer's six-digit pickup code" required><button class="btn btn-primary">Confirm pickup</button></form></div></c:otherwise></c:choose>
                            </c:otherwise>
                        </c:choose>
                    </article>
                </c:forEach>
            </div></c:otherwise>
        </c:choose>
    </section>
</div>

<%@ include file="_footer.jsp" %>
