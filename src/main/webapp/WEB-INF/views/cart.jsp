<%@ include file="_header.jsp" %>

<h1 style="font-size:30px;font-weight:800;" class="mb-3">Your Cart</h1>

<c:choose>
    <c:when test="${empty items}">
        <div class="surface pad text-center">
            <p class="mb-3 text-muted">Your cart is empty.</p>
            <a class="btn btn-primary" href="${ctx}/browse">Browse listings</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-4">
            <div class="col-lg-8">
                <div class="table-responsive">
                    <table class="table cart-table align-middle mb-0">
                        <thead>
                        <tr><th colspan="2">Item</th><th>Seller</th><th class="text-end">Price</th><th></th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="l" items="${items}">
                            <tr>
                                <td style="width:72px;">
                                    <img src="${ctx}/img/${l.image}" alt="" width="60" height="46"
                                         style="object-fit:cover;border-radius:10px;border:1px solid var(--line);">
                                </td>
                                <td>
                                    <a class="fw-semibold text-dark" href="${ctx}/listing?id=${l.id}"><c:out value="${l.title}"/></a>
                                    <c:if test="${not l.available}"><div class="text-danger small">No longer available. Please remove this item.</div></c:if>
                                    <div><span class="cat-chip"><c:out value="${l.category}"/></span></div>
                                </td>
                                <td class="text-muted"><c:out value="${l.sellerName}"/></td>
                                <td class="text-end fw-bold">&#8377;<fmt:formatNumber value="${l.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td class="text-end">
                                    <form method="post" action="${ctx}/cart">
<input type="hidden" name="csrfToken" value="${csrfToken}">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="id" value="${l.id}">
                                        <input type="hidden" name="back" value="/cart">
                                        <button class="btn btn-sm btn-outline-danger" type="submit">Remove</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="col-lg-4">
                <div class="surface pad">
                    <h5 class="mb-3">Order summary</h5>
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Items</span><span class="fw-semibold">${items.size()}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-3">
                        <span class="text-muted">Total</span>
                        <span class="price">&#8377;<fmt:formatNumber value="${total}" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                    <hr>
                    <c:choose>
                        <c:when test="${empty currentStudent}">
                            <div class="alert alert-warning">
                                This is a <strong>guest cart</strong> (saved in a cookie). Log in to check out &mdash;
                                your items move into your account automatically.
                            </div>
                            <a class="btn btn-primary w-100" href="${ctx}/login">Log in to checkout</a>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${ctx}/checkout" id="payment-form">
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <div class="payment-choice">
                                    <label><input type="radio" name="paymentMethod" value="UPI" checked> <strong>UPI</strong><span>Pay using your UPI ID</span></label>
                                    <label><input type="radio" name="paymentMethod" value="NET_BANKING"> <strong>Net banking</strong><span>Continue with your bank</span></label>
                                </div>
                                <div id="upi-fields" class="mt-3"><label class="form-label" for="upiId">UPI ID</label><input id="upiId" name="upiId" class="form-control" placeholder="yourname@bank" maxlength="100" required></div>
                                <div id="bank-fields" class="mt-3 d-none"><label class="form-label" for="bankCode">Choose bank</label><select id="bankCode" name="bankCode" class="form-select" disabled required><option value="">Select your bank</option><option value="SBI">State Bank of India</option><option value="HDFC">HDFC Bank</option><option value="ICICI">ICICI Bank</option><option value="AXIS">Axis Bank</option><option value="KOTAK">Kotak Mahindra Bank</option><option value="OTHER">Other bank</option></select></div>
                                <details class="purchase-terms mt-3">
                                    <summary>Purchase terms and buyer protection</summary>
                                    <ul>
                                        <li>Inspect the item and confirm its condition during campus pickup.</li>
                                        <li>You receive a private six-digit pickup code. Share it with the seller only after inspection and handover.</li>
                                        <li>The seller's verified profile name, email and phone number appear after checkout for pickup coordination.</li>
                                        <li>CampusMarket blocks prohibited goods and records the agreed price and payment reference.</li>
                                        <li>This student project simulates payment confirmation. Any refund or pickup change must be agreed directly with the seller.</li>
                                        <li>Use contact details only for this order and never share them outside the transaction.</li>
                                    </ul>
                                </details>
                                <label class="terms-check mt-3">
                                    <input type="checkbox" name="acceptTerms" value="yes" required>
                                    <span>I have read and agree to the purchase terms and contact-data rules.</span>
                                </label>
                                <p class="payment-note">Academic payment simulation: no bank password, OTP or account number is collected. A payment reference is recorded with the order.</p>
                                <button class="btn btn-primary btn-lg w-100" type="submit">Confirm payment &amp; place order</button>
                            </form>
                            <script>
                              document.querySelectorAll('input[name="paymentMethod"]').forEach(function(r){r.addEventListener('change',function(){var upi=this.value==='UPI';document.getElementById('upi-fields').classList.toggle('d-none',!upi);document.getElementById('bank-fields').classList.toggle('d-none',upi);document.getElementById('upiId').disabled=!upi;document.getElementById('bankCode').disabled=upi;});});
                            </script>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="_footer.jsp" %>
