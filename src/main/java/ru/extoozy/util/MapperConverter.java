package ru.extoozy.util;

import org.mapstruct.Named;
import ru.extoozy.enums.TransactionType;

import java.math.BigDecimal;

public class MapperConverter {

    @Named("transactionTypeStrToEnum")
    public TransactionType transactionTypeStrToEnum(Object transactionType) {
        return TransactionType.valueOf(transactionType.toString());
    }

    public String mapToString(Object value) {
        return value != null ? value.toString() : null;
    }

    public Long mapToLong(Object value) {
        if (value.getClass().equals(Long.class)) {
            return (Long) value;
        }
        return Long.valueOf((Integer) value);
    }

    public boolean mapToBoolean(Object value) {
        return (Boolean) value;
    }

    public BigDecimal mapToBigDecimal(Object value) {
        if (value.getClass().equals(Integer.class)) {
            return BigDecimal.valueOf((Integer) value);
        }
        if (value.getClass().equals(Double.class)) {
            return BigDecimal.valueOf((Double) value);
        }
        return (BigDecimal) value;
    }
}
