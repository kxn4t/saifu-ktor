package com.saifu.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class Money (
    val id: String,
    val mode: String,
    @JsonProperty("user_id")
    val userId: String,
    val date: String,
    @JsonProperty("category_id")
    val categoryId: String,
    @JsonProperty("genre_id")
    val genreId: String,
    @JsonProperty("to_account_id")
    val toAccountId: String,
    @JsonProperty("from_account_id")
    val fromAccountId: String,
    val amount: BigDecimal,
    val comment: String,
    val active: String,
    val name: String,
    @JsonProperty("receipt_id")
    val receiptId: String,
    val place: String,
    val created: String,
    @JsonProperty("currency_code")
    val currencyCode: String
)

data class MoneyResponse (
    @JsonProperty("money")
    val moneys: List<Money>,
    val requested: String
)
