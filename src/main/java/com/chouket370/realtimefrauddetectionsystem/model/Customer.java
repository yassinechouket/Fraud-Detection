package com.chouket370.realtimefrauddetectionsystem.model;


import com.chouket370.realtimefrauddetectionsystem.enums.Category;
import com.chouket370.realtimefrauddetectionsystem.enums.Currency;
import com.chouket370.realtimefrauddetectionsystem.enums.Merchant;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Random;

@Document(collection = "customers")

@NoArgsConstructor
@Data

public class Customer {
    @Id
    private String id;
    private  String userId;
    private  List<Merchant> merchants;
    private  List<Category> categories;
    private  Double meanSpending;
    private  Double spendingStdDev;
    private  Currency preferredCurrency;

    public Customer(String userId, List<Merchant> merchants, List<Category> categories,
                    Double meanSpending, Double spendingStdDev, Currency preferredCurrency) {
        this.userId = userId;
        this.merchants = merchants;
        this.categories = categories;
        this.meanSpending = meanSpending;
        this.spendingStdDev = spendingStdDev;
        this.preferredCurrency = preferredCurrency;
    }

    public Category getFrequentCategory(){
        Random random = new Random();
        return categories.get(random.nextInt(categories.size()));
    }

    public Category getUnfrequentCategory(){
        List<Category> allCategories=List.of(Category.values());

        List<Category>infrequentCategories =allCategories.stream()
                .filter(category -> !categories.contains(category)).toList();

        Random random = new Random();

        return infrequentCategories.get(random.nextInt(infrequentCategories.size()));
    }

    public Currency getRandomSuspiciousCurrency(){
        List<Currency> allCurrency = List.of(Currency.values());

        List<Currency> infrequentCurrency=allCurrency.stream().filter(
                currency->!(currency==preferredCurrency )).toList()
        ;
        Random random = new Random();
        return infrequentCurrency.get(random.nextInt(infrequentCurrency.size()));

    }

}
