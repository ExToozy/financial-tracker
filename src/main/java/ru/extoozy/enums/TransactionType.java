package ru.extoozy.enums;

import java.math.BigDecimal;

public enum TransactionType {

    REPLENISHMENT(BigDecimal.valueOf(1)),

    WITHDRAWAL(BigDecimal.valueOf(-1));


    private final BigDecimal amountSign;

    TransactionType(BigDecimal amountSign) {
        this.amountSign = amountSign;
    }

    public BigDecimal getSign() {
        return amountSign;
    }
}
