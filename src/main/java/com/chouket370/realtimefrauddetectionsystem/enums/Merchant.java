package com.chouket370.realtimefrauddetectionsystem.enums;

import java.util.List;
import java.util.Map;
import java.util.Random;

public enum Merchant {
    AMAZON, WALMART, BEST_BUY, TARGET, COSTCO, ETSY, EBAY, IKEA,
    APPLE, MICROSOFT, GOOGLE,
    DUNNES_STORES, LIDL, TESCO;

    private static final Random RANDOM = new Random();

    private static final Map<Category, List<Merchant>> CATEGORY_MERCHANTS;

    static {
        CATEGORY_MERCHANTS = Map.of(
                Category.RETAIL, List.of(AMAZON, WALMART, BEST_BUY, TARGET, COSTCO, ETSY, EBAY, IKEA),
                Category.TECH, List.of(APPLE, MICROSOFT, GOOGLE),
                Category.GROCERY, List.of(DUNNES_STORES, LIDL, TESCO)

        );
    }

    public static Merchant getRandomMerchant(Category category) {
        List<Merchant> merchants = CATEGORY_MERCHANTS.get(category);
        if (merchants == null || merchants.isEmpty()) {
            throw new IllegalStateException("No merchants for category: " + category);
        }
        return merchants.get(RANDOM.nextInt(merchants.size()));
    }
}
