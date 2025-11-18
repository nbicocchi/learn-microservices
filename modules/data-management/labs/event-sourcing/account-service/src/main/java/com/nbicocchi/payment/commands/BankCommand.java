package com.nbicocchi.payment.commands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateAccount.class, name = "CreateAccount"),
        @JsonSubTypes.Type(value = DepositMoney.class, name = "DepositMoney"),
        @JsonSubTypes.Type(value = WithdrawMoney.class, name = "WithdrawMoney")
})
public interface BankCommand {
    String accountId();
}
