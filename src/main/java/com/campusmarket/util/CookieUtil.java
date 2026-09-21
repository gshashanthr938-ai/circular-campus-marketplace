package com.campusmarket.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Helpers for reading/writing cookies used by GUEST features:
 *   - "guest_cart"      : items a not-logged-in visitor adds to their cart
 *   - "recently_viewed" : listing ids a visitor has recently opened
 *
 * These live in cookies (client side) precisely to contrast with the
 * logged-in identity and cart, which live in the server-side HttpSession / DB.
 */
public final class CookieUtil {

    public static final String GUEST_CART = "guest_cart";
    public static final String RECENTLY_VIEWED = "recently_viewed";
    private static final int MAX_RECENT = 5;
    private static final int ONE_WEEK = 7 * 24 * 60 * 60;

    private CookieUtil() {
    }

    public static String get(HttpServletRequest req, String name) {
        if (req.getCookies() == null) {
            return null;
        }
        for (Cookie c : req.getCookies()) {
            if (c.getName().equals(name)) {
                return c.getValue();
            }
        }
        return null;
    }

    public static void set(HttpServletResponse resp, String name, String value) {
        Cookie c = new Cookie(name, value == null ? "" : value);
        c.setPath("/");
        c.setMaxAge(ONE_WEEK);
        c.setHttpOnly(true);
        resp.addCookie(c);
    }

    public static void delete(HttpServletResponse resp, String name) {
        Cookie c = new Cookie(name, "");
        c.setPath("/");
        c.setMaxAge(0);
        resp.addCookie(c);
    }

    /** Parse the RFC6265-safe dot format and legacy comma format into listing ids. */
    public static List<Long> parseIds(String cookieValue) {
        List<Long> ids = new ArrayList<>();
        if (cookieValue == null || cookieValue.isEmpty()) {
            return ids;
        }
        for (String part : cookieValue.split("[.,]")) {
            part = part.trim();
            if (!part.isEmpty()) {
                try {
                    ids.add(Long.parseLong(part));
                } catch (NumberFormatException ignore) {
                    // skip malformed entries
                }
            }
        }
        return ids;
    }

    public static String joinIds(List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(ids.get(i));
        }
        return sb.toString();
    }

    /** Add an id to the guest cart cookie (no duplicates). */
    public static void addToGuestCart(HttpServletRequest req, HttpServletResponse resp, long listingId) {
        Set<Long> ids = new LinkedHashSet<>(parseIds(get(req, GUEST_CART)));
        ids.add(listingId);
        set(resp, GUEST_CART, joinIds(new ArrayList<>(ids)));
    }

    public static void removeFromGuestCart(HttpServletRequest req, HttpServletResponse resp, long listingId) {
        List<Long> ids = parseIds(get(req, GUEST_CART));
        ids.remove(Long.valueOf(listingId));
        set(resp, GUEST_CART, joinIds(ids));
    }

    /** Record a viewed listing id at the front of the recently-viewed cookie (capped, no dups). */
    public static void recordRecentlyViewed(HttpServletRequest req, HttpServletResponse resp, long listingId) {
        List<Long> ids = parseIds(get(req, RECENTLY_VIEWED));
        ids.remove(Long.valueOf(listingId)); // move to front if already present
        ids.add(0, listingId);
        while (ids.size() > MAX_RECENT) {
            ids.remove(ids.size() - 1);
        }
        set(resp, RECENTLY_VIEWED, joinIds(ids));
    }
}
