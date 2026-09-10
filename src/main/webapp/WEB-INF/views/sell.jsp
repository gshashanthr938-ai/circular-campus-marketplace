<%@ include file="_header.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-7">
        <h2 class="mb-3">${empty listing.id or listing.id == 0 ? 'List an item for resale' : 'Edit listing'}</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form method="post" action="${ctx}/sell" class="listing-card p-4">
            <c:if test="${not empty listing.id and listing.id > 0}">
                <input type="hidden" name="id" value="${listing.id}">
            </c:if>

            <div class="mb-3">
                <label class="form-label">Title</label>
                <input type="text" name="title" class="form-control" required
                       value="<c:out value='${listing.title}'/>">
            </div>
            <div class="mb-3">
                <label class="form-label">Description</label>
                <textarea name="description" class="form-control" rows="3"><c:out value="${listing.description}"/></textarea>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Category</label>
                    <input type="text" name="category" class="form-control" list="catlist" required
                           value="<c:out value='${listing.category}'/>">
                    <datalist id="catlist">
                        <option value="Books">
                        <option value="Electronics">
                        <option value="Furniture">
                        <option value="Hostel Essentials">
                        <option value="Clothing">
                        <option value="Sports">
                    </datalist>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Condition</label>
                    <select name="condition" class="form-select" required>
                        <c:set var="cond" value="${listing.condition}" />
                        <option value="" ${empty cond ? 'selected' : ''} disabled>Choose...</option>
                        <option ${cond == 'Like New' ? 'selected' : ''}>Like New</option>
                        <option ${cond == 'Good' ? 'selected' : ''}>Good</option>
                        <option ${cond == 'Fair' ? 'selected' : ''}>Fair</option>
                    </select>
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">Price (&#8377;)</label>
                <input type="number" step="0.01" min="0" name="price" class="form-control" required
                       value="<c:out value='${listing.price}'/>">
            </div>
            <button class="btn btn-brand" type="submit">
                ${empty listing.id or listing.id == 0 ? 'Publish listing' : 'Save changes'}
            </button>
            <a class="btn btn-link" href="${ctx}/my-listings">Cancel</a>
        </form>
    </div>
</div>

<%@ include file="_footer.jsp" %>
