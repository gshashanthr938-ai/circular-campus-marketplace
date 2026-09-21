<%@ include file="_header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-5">
        <h2 class="mb-3">Create your account</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form method="post" action="${ctx}/register" class="listing-card p-4">
<input type="hidden" name="csrfToken" value="${csrfToken}">
            <div class="mb-3">
                <label class="form-label">Full name</label>
                <input type="text" name="name" class="form-control" required
                       value="<c:out value='${name}'/>">
            </div>
            <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" name="email" class="form-control" required
                       value="<c:out value='${email}'/>">
                <div class="form-text">Shared with a buyer only after a completed purchase.</div>
            </div>
            <div class="mb-3">
                <label class="form-label">Phone number</label>
                <input type="tel" name="phone" class="form-control" required maxlength="20"
                       placeholder="+91 98765 43210" value="<c:out value='${phone}'/>">
                <div class="form-text">Used for pickup coordination after a sale.</div>
            </div>
            <div class="mb-3">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="form-control" required minlength="8">
            </div>
            <button class="btn btn-brand w-100" type="submit">Create student account</button>
        </form>
        <p class="mt-3 text-center">Already have an account? <a href="${ctx}/login">Log in</a></p>
    </div>
</div>

<%@ include file="_footer.jsp" %>
