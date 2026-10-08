<%@ include file="_header.jsp" %>

<div class="wallet-hero">
    <div>
        <span class="eyebrow">Refund wallet</span>
        <h2>Your money, your choice</h2>
        <p>New accounts start at &#8377;0. This wallet only holds refunds that you choose to receive here.</p>
    </div>
    <div class="wallet-balance-card">
        <small>Available to withdraw</small>
        <strong>&#8377;<fmt:formatNumber value="${student.walletBalance}" minFractionDigits="2" maxFractionDigits="2"/></strong>
        <span>Protected by an auditable transaction ledger</span>
    </div>
</div>

<div class="refund-explainer mb-4" aria-label="How refunds work">
    <div><b>1</b><span><strong>Cancel before pickup</strong><small>Open History and choose the order.</small></span></div>
    <div><b>2</b><span><strong>Choose destination</strong><small>Original UPI/bank source or this wallet.</small></span></div>
    <div><b>3</b><span><strong>Withdraw if needed</strong><small>Move wallet refunds through UPI or net banking.</small></span></div>
</div>

<div class="wallet-layout">
    <section class="surface pad wallet-withdraw">
        <h4>Withdraw refund money</h4>
        <p class="text-muted">Choose where to send the available wallet balance.</p>
        <form method="post" action="${ctx}/wallet" id="withdrawForm">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <label class="form-label" for="withdrawAmount">Amount</label>
            <div class="input-group mb-3"><span class="input-group-text">&#8377;</span><input id="withdrawAmount" name="amount" type="number" min="0.01" step="0.01" max="${student.walletBalance}" class="form-control" placeholder="0.00" required></div>
            <fieldset class="payment-choice mb-3">
                <legend class="form-label">Withdraw through</legend>
                <label><input type="radio" name="method" value="UPI" checked><strong>UPI</strong><span>Send to a verified UPI ID.</span></label>
                <label><input type="radio" name="method" value="NET_BANKING"><strong>Net banking</strong><span>Send through your selected bank.</span></label>
            </fieldset>
            <div id="upiDestination"><label class="form-label" for="upiId">UPI ID</label><input id="upiId" name="destinationDetail" class="form-control mb-3" placeholder="name@bank" autocomplete="off" required></div>
            <div id="bankDestination" hidden><label class="form-label" for="bankName">Bank</label><select id="bankName" class="form-select mb-3"><option value="">Choose bank</option><option>SBI</option><option>HDFC</option><option>ICICI</option><option>AXIS</option><option>KOTAK</option><option>OTHER</option></select></div>
            <button class="btn btn-primary w-100" ${student.walletBalance <= 0 ? 'disabled' : ''}>Withdraw money</button>
            <p class="payment-note mb-0">Academic payment simulation: no real bank transfer, password or OTP is used. The reference and balance change are recorded atomically.</p>
        </form>
    </section>

    <section>
        <div class="wallet-ledger-head"><div><h4>Wallet activity</h4><p>Every balance change has a reference.</p></div><a href="${ctx}/history">Order history</a></div>
        <c:choose>
            <c:when test="${empty walletEntries}"><div class="surface pad wallet-empty"><strong>No wallet activity yet</strong><p>Choose “Campus wallet” when requesting a refund and it will appear here instantly.</p></div></c:when>
            <c:otherwise><div class="wallet-ledger">
                <c:forEach var="entry" items="${walletEntries}">
                    <article class="wallet-entry">
                        <div class="wallet-entry-icon ${entry.credit ? 'credit' : 'debit'}">${entry.credit ? '+' : '&minus;'}</div>
                        <div><strong><c:out value="${entry.entryType == 'REFUND_CREDIT' ? 'Refund received' : 'Withdrawal'}"/></strong><span><c:out value="${entry.details}"/></span><small><c:out value="${entry.reference}"/> &middot; <fmt:formatDate value="${entry.createdAt}" pattern="dd MMM yyyy, HH:mm"/></small></div>
                        <b class="${entry.credit ? 'credit-text' : 'debit-text'}">${entry.credit ? '+' : ''}&#8377;<fmt:formatNumber value="${entry.amount}" minFractionDigits="2" maxFractionDigits="2"/></b>
                    </article>
                </c:forEach>
            </div></c:otherwise>
        </c:choose>
    </section>
</div>

<script>
(() => {
  const radios = document.querySelectorAll('input[name="method"]');
  const upi = document.getElementById('upiDestination');
  const bank = document.getElementById('bankDestination');
  const upiInput = document.getElementById('upiId');
  const bankInput = document.getElementById('bankName');
  function sync() {
    const useUpi = document.querySelector('input[name="method"]:checked').value === 'UPI';
    upi.hidden = !useUpi; bank.hidden = useUpi;
    upiInput.required = useUpi; bankInput.required = !useUpi;
    if (useUpi) { upiInput.name = 'destinationDetail'; bankInput.removeAttribute('name'); }
    else { bankInput.name = 'destinationDetail'; upiInput.removeAttribute('name'); }
  }
  radios.forEach(r => r.addEventListener('change', sync)); sync();
})();
</script>

<%@ include file="_footer.jsp" %>
