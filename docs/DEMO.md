# Professor demonstration

1. Start using run-mysql.ps1; open http://localhost:8080/browse.
2. Show categories and price filters. Add another student's available item as a guest.
3. Log in and show the preserved cart.
4. Open the buyer-protection terms, select UPI or net banking, accept the terms and complete the academic payment confirmation.
5. Show the payment reference and the seller's unlocked email and phone number in history, then show the SOLD status and competing-buyer waitlist alert.
6. Explain cookies, sessions, Servlets, JSP, JDBC and atomic database transactions.
7. Log in as the administrator and open the control centre. Show live students, active inventory, completed resales, transaction value, category mix, payment mix, waitlist count and estimated CO2e avoided.
8. Show the automated tests and source repository.

Read-only database queries:

```sql
SELECT student_id, name, email, phone, role, sustainability_points FROM students;
SELECT listing_id, title, category, price, status FROM listings;
SELECT txn_id, buyer_id, listing_id, amount, payment_method, payment_reference, payment_status, terms_accepted_at, txn_date FROM transactions;
SELECT listing_id, image_path, position_no FROM listing_images ORDER BY listing_id, position_no;
SELECT cart_id, session_id, listing_id FROM cart_items;
```

The payment gateway is simulated and never requests a bank password or OTP. Seller contact details stay private while browsing and unlock only for the completed buyer. Seller uploads show the actual item; bundled catalog photographs are references. If using H2 for preview, identify it accurately rather than calling it MySQL.
