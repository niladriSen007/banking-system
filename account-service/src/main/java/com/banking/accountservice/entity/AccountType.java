package com.banking.accountservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

public enum AccountType {
    SAVINGS,
    CURRENT,
    FIXED_DEPOSIT
}