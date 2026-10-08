# Professor demonstration

1. Start using run-mysql.ps1; open http://localhost:8080/browse.
2. Show categories and price filters. Add another student's available item as a guest.
3. Log in and show the preserved cart.
4. Open the buyer-protection terms, select UPI or net banking, accept the terms and complete the academic payment confirmation.
5. In buyer history, show the seller's unlocked contact and the private pickup code. Explain that the buyer shares it only after inspecting the item.
6. For one uncollected order, open **Need to cancel this order?** Select original payment source or CampusMarket wallet and show the refund reference.
7. Open **Wallet**. Explain that signup gives ₹0, only wallet-selected refunds create balance, and withdrawals use UPI or net banking. Show both ledger entries.
8. For a different order, open seller history and enter the pickup code. Return to the buyer account to show the verified handover and newly unlocked review form.
9. Explain the two-buyer test: the conditional `AVAILABLE` → `SOLD` database update allows exactly one winner; the losing transaction rolls back and that buyer joins the waitlist.
10. Log in as the administrator and open the control centre. Show live students, active inventory, completed resales, verified handovers, category/payment mix, waitlists and estimated CO2e avoided.
11. Show the automated tests and source repository.

Read-only database queries:

```sql
SELECT student_id, name, email, phone, role, wallet_balance, sustainability_points FROM students;
SELECT listing_id, title, category, price, status FROM listings;
SELECT txn_id, buyer_id, listing_id, amount, payment_method, payment_reference, payment_status, refund_method, refund_reference, fulfillment_status, refunded_at, pickup_completed_at FROM transactions;
SELECT student_id, entry_type, amount, destination, reference, created_at FROM wallet_transactions ORDER BY created_at DESC;
SELECT listing_id, image_path, position_no FROM listing_images ORDER BY listing_id, position_no;
SELECT cart_id, session_id, listing_id FROM cart_items;
```

The payment, original-source refund and withdrawal gateway are simulated and never request a bank password or OTP. Database balances, statuses and references are real application records. Seller contact details stay private while browsing and unlock only for the completed buyer. Seller uploads show the actual item; bundled catalog photographs are references. If using H2 for preview, identify it accurately rather than calling it MySQL.
