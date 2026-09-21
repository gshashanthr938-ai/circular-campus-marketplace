<%@ include file="_header.jsp" %>

<div class="row justify-content-center">
    <div class="col-lg-7">
        <h2 class="mb-3">${empty listing.id or listing.id == 0 ? 'List an item for resale' : 'Edit listing'}</h2>
        <p class="text-muted">Listings are checked for prohibited items and unusually high prices before publishing.</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form method="post" action="${ctx}/sell" enctype="multipart/form-data" class="listing-card p-4">
<input type="hidden" name="csrfToken" value="${csrfToken}">
            <c:if test="${not empty listing.id and listing.id > 0}">
                <input type="hidden" name="id" value="${listing.id}">
            </c:if>

            <div class="mb-3">
                <label class="form-label">Title</label>
                <input type="text" name="title" class="form-control" required
                       value="<c:out value='${listing.title}'/>">
            </div>
            <div class="alert alert-light small">
                Campus safety rules do not allow tobacco, vapes, alcohol, weapons, narcotics or fireworks.
                The price check uses category, condition and campus resale limits; it does not contact an external AI service.
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
                        <option value="Stationery">
                        <option value="Audio">
                        <option value="Bags">
                        <option value="Music">
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
            <div class="mb-3">
                <label class="form-label">Product pictures <span class="text-muted">(1&ndash;3)</span></label>
                <input type="file" name="photo1" class="form-control mb-2" accept="image/jpeg,image/png,image/webp" ${empty listing.id or listing.id == 0 ? 'required' : ''}>
                <input type="file" name="photo2" class="form-control mb-2" accept="image/jpeg,image/png,image/webp">
                <input type="file" name="photo3" class="form-control" accept="image/jpeg,image/png,image/webp">
                <div class="form-text">Use clear photos of the actual item. JPG, PNG or WebP; maximum 3 MB each. New uploads while editing replace the current uploaded set.</div>
                <c:if test="${not empty listingImages}"><div class="upload-preview mt-3"><c:forEach var="img" items="${listingImages}"><img src="${ctx}/img/${img}" alt="Current product picture"></c:forEach></div></c:if>
            </div>
            <button class="btn btn-brand" type="submit">
                ${empty listing.id or listing.id == 0 ? 'Publish listing' : 'Save changes'}
            </button>
            <a class="btn btn-link" href="${ctx}/my-listings">Cancel</a>
        </form>
    </div>
</div>

<%@ include file="_footer.jsp" %>
