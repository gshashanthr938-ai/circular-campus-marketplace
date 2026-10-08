# CampusMarket Manual Test Cases

## Test Environment

- Application: CampusMarket
- Browser: Google Chrome
- Backend: Java 17, Servlets and Apache Tomcat
- Database: H2 for local demonstration or MySQL
- Demo password: `password`

## Test Cases

### TC-01: Student Registration

**Steps:**

1. Open the registration page.
2. Enter a new name, email address, phone number and password.
3. Select **Create account**.

**Expected result:**

- A new student account is created.
- The student is logged in automatically.
- No wallet money is added to the account.
- The navigation bar displays the student’s name.

**Result:** Pass / Fail

---

### TC-02: Student Login and Logout

**Steps:**

1. Open the login page.
2. Enter `rahul@campus.edu`.
3. Enter the password `password`.
4. Select **Login**.
5. Select **Logout**.

**Expected result:**

- The correct account is opened.
- A new authenticated session is created.
- Logout destroys the authenticated session.
- Protected pages redirect the user to login after logout.

**Result:** Pass / Fail

---

### TC-03: Guest-Cart Migration

**Steps:**

1. Open CampusMarket without logging in.
2. Add an available product to the cart.
3. Confirm that the guest cart shows the product.
4. Log in using a student account.
5. Open the cart again.

**Expected result:**

- The cart works before login using a cookie.
- The selected product remains in the cart after login.
- The guest-cart item is migrated to the logged-in session cart.
- The guest-cart cookie is cleared after migration.

**Result:** Pass / Fail

---

### TC-04: Product Creation with Images

**Steps:**

1. Log in using a student account.
2. Select **Sell**.
3. Enter the product title, description, category, condition and price.
4. Upload one to three JPG, PNG or WebP photographs.
5. Select **Publish listing**.
6. Open the newly created product.

**Expected result:**

- The product is successfully published.
- The correct seller is attached to the listing.
- The uploaded photographs appear on the product page.
- More than three images or unsupported files are rejected.
- Each uploaded image must be no larger than 3 MB.

**Result:** Pass / Fail

---

### TC-05: Prohibited-Item Rejection

**Steps:**

1. Log in and open the selling form.
2. Enter `Vape kit` as the product title.
3. Enter a description, category, condition and price.
4. Upload an image.
5. Select **Publish listing**.

**Expected result:**

- The listing is rejected.
- A prohibited-item warning is displayed.
- The product is not saved in the marketplace.
- Similar checks should reject cigarettes, alcohol, weapons, narcotics and fireworks.

**Result:** Pass / Fail

---

### TC-06: UPI Checkout

**Steps:**

1. Log in as a buyer.
2. Add another student’s available product to the cart.
3. Open the cart.
4. Select **UPI**.
5. Enter `student@bank`.
6. Read and accept the purchase terms.
7. Select **Confirm payment and place order**.
8. Open transaction history.

**Expected result:**

- The checkout succeeds.
- A payment reference is generated.
- The payment method is recorded as UPI.
- The terms-acceptance time is recorded.
- The listing changes to SOLD.
- Seller contact details and a pickup code appear in buyer history.

**Result:** Pass / Fail

---

### TC-07: Net-Banking Checkout

**Steps:**

1. Add an available product to the cart.
2. Select **Net banking**.
3. Choose a bank.
4. Accept the purchase terms.
5. Confirm the payment.
6. Open transaction history.

**Expected result:**

- The checkout succeeds.
- The selected payment method is recorded as net banking.
- A payment reference is generated.
- The application does not request a bank password, account number or OTP.

**Result:** Pass / Fail

---

### TC-08: Concurrent Purchase and Waitlist

**Requirements:**

- Use two different browser sessions.
- Log in using two different buyer accounts.
- Select the same available product.

**Steps:**

1. Add the same product to both buyers’ carts.
2. Attempt checkout from both sessions at nearly the same time.
3. Check transaction history for both buyers.
4. Check the losing buyer’s notifications.

**Expected result:**

- Only one buyer successfully purchases the product.
- Only one completed transaction is created.
- The listing becomes SOLD.
- The other buyer is added to the waitlist.
- The losing buyer receives an in-app notification.

**Result:** Pass / Fail

---

### TC-09: Pickup-Code Verification

**Steps:**

1. Complete a purchase.
2. Open the buyer’s transaction history.
3. Note the private six-digit pickup code.
4. Log in as the seller.
5. Open **History → My Sales**.
6. Enter an incorrect code first.
7. Enter the correct code after the buyer inspects the product.

**Expected result:**

- An incorrect code does not confirm the handover.
- Only the correct seller can confirm the transaction.
- The correct code changes the status to PICKUP_COMPLETED.
- The pickup-completion date and time are recorded.
- The buyer sees **Handover verified**.

**Result:** Pass / Fail

---

### TC-10: Review Submission

**Steps:**

1. Open buyer transaction history before pickup confirmation.
2. Check whether the review form is available.
3. Complete pickup-code confirmation.
4. Return to buyer history.
5. Submit a rating from 1–5 and a comment.
6. Open the product page.

**Expected result:**

- The buyer cannot review before pickup confirmation.
- The review form appears after confirmed pickup.
- Only one review is permitted for each transaction.
- The rating and comment appear on the product page.
- The seller’s average rating is updated.

**Result:** Pass / Fail

---

### TC-11: Administrator Dashboard

**Steps:**

1. Log in using `asha@campus.edu`.
2. Enter the password `password`.
3. Open **Admin**.
4. Review the analytics and listing-moderation section.
5. Change a listing’s moderation note or status if required.

**Expected result:**

- Only the administrator can open the dashboard.
- The dashboard displays registered students, listings, completed transactions, verified handovers, waitlists, ratings and estimated CO2e avoided.
- Category and payment-method statistics are visible.
- The administrator can moderate listing status, photographs and notes.

**Result:** Pass / Fail

---

### TC-12: Buyer Refund Choice

**Steps:**

1. Complete a purchase but do not confirm pickup.
2. Open **History** and expand **Need to cancel this order?**
3. Choose **CampusMarket wallet**, enter a reason and confirm the refund.
4. Repeat with a different order and choose **Original payment source**.

**Expected result:**

- Each order can be refunded only once and only by its buyer.
- Pickup-confirmed orders cannot be refunded automatically.
- The transaction becomes REFUNDED/CANCELLED and receives a refund reference.
- The listing becomes AVAILABLE again and waitlisted students are notified.
- A wallet refund increases the wallet balance; an original-source refund does not.
- The original purchase sustainability points are removed.

**Result:** Pass / Fail

---

### TC-13: Refund-Wallet Withdrawal

**Steps:**

1. Open **Wallet** after completing a wallet refund.
2. Confirm that the refund credit and reference appear in the ledger.
3. Enter an amount smaller than the available balance.
4. Choose UPI or net banking and confirm the withdrawal.
5. Attempt another withdrawal larger than the remaining balance.

**Expected result:**

- The successful withdrawal reduces the balance exactly once and creates a debit reference.
- The ledger shows both the refund credit and withdrawal.
- The UPI identifier is masked in wallet history.
- The overdraw attempt is rejected and the balance cannot become negative.
- A new account has ₹0 until it receives a wallet-selected refund.

**Result:** Pass / Fail

## Final Test Summary

| Test Case | Feature | Status |
|---|---|---|
| TC-01 | Registration | Pass |
| TC-02 | Login and logout | Pass |
| TC-03 | Guest-cart migration | Pass |
| TC-04 | Product creation and images | Pass |
| TC-05 | Prohibited-item rejection | Pass |
| TC-06 | UPI checkout | Pass |
| TC-07 | Net-banking checkout | Pass |
| TC-08 | Concurrent checkout and waitlist | Pass |
| TC-09 | Pickup-code verification | Pass |
| TC-10 | Review submission | Pass |
| TC-11 | Administrator dashboard | Pass |
| TC-12 | Buyer refund choice | Pass |
| TC-13 | Refund-wallet withdrawal | Pass |
