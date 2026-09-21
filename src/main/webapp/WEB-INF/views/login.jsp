<%@ include file="_header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-5">
        <h2 class="mb-3">Log in</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form method="post" action="${ctx}/login" class="listing-card p-4">
<input type="hidden" name="csrfToken" value="${csrfToken}">
            <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" name="email" class="form-control" required
                       value="<c:out value='${email}'/>">
            </div>
            <div class="mb-3">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="form-control" required>
            </div>
            <button class="btn btn-brand w-100" type="submit">Log in</button>
        </form>

        <div class="alert alert-secondary mt-3 small mb-0">
            <strong>Demo accounts</strong> (password for all: <code>password</code>):<br>
            asha@campus.edu &middot; rahul@campus.edu &middot; neha@campus.edu
        </div>
        <p class="mt-3 text-center">New here? <a href="${ctx}/register">Create an account</a></p>
    </div>
</div>

<%@ include file="_footer.jsp" %>
