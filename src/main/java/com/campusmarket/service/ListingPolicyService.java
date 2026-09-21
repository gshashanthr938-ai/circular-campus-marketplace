package com.campusmarket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Transparent local moderation and price guidance; no external AI service is called. */
public final class ListingPolicyService {
    private static final Set<String> PROHIBITED = Set.of(
            "cigarette", "cigarettes", "tobacco", "vape", "vapes", "vaping", "nicotine",
            "liquor", "alcohol", "beer", "wine", "whiskey", "whisky", "vodka", "rum",
            "weapon", "gun", "pistol", "ammunition", "narcotic", "narcotics", "fireworks");
    private static final Map<String, BigDecimal> CAPS = new LinkedHashMap<>();
    static {
        CAPS.put("Books", bd("3000")); CAPS.put("Stationery", bd("1500"));
        CAPS.put("Audio", bd("12000")); CAPS.put("Electronics", bd("80000"));
        CAPS.put("Furniture", bd("20000")); CAPS.put("Hostel Essentials", bd("8000"));
        CAPS.put("Clothing", bd("10000")); CAPS.put("Sports", bd("20000"));
        CAPS.put("Music", bd("50000")); CAPS.put("Bags", bd("12000"));
    }

    public record Decision(boolean allowed, String message, BigDecimal suggestedMaximum) {}

    public Decision evaluate(String title, String description, String category,
                             BigDecimal price, String condition) {
        String text = ((title == null ? "" : title) + " " + (description == null ? "" : description))
                .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
        for (String word : text.split(" +")) {
            if (PROHIBITED.contains(word))
                return new Decision(false, "This item is prohibited on the campus marketplace.", null);
        }
        BigDecimal base = CAPS.get(category);
        if (base == null)
            return new Decision(false, "Choose one of the supported marketplace categories.", null);
        BigDecimal factor = "Fair".equals(condition) ? bd("0.55") : "Good".equals(condition) ? bd("0.80") : BigDecimal.ONE;
        BigDecimal cap = base.multiply(factor).setScale(0, RoundingMode.HALF_UP);
        if (price != null && price.compareTo(cap) > 0)
            return new Decision(false, "The price check found this too high for the category and condition. Maximum accepted: INR " + cap + ".", cap);
        return new Decision(true, "Price is within the accepted campus range (up to INR " + cap + ").", cap);
    }

    public static Map<String, BigDecimal> categoryCaps() { return Map.copyOf(CAPS); }
    private static BigDecimal bd(String value) { return new BigDecimal(value); }
}
