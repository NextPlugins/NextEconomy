package com.nextplugins.economy.api.model.account.old;

import com.nextplugins.economy.api.model.account.Account;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Builder
public class OldAccount {
    
    private final UUID uuid;
    private final String name;
    private final double balance;

    public Account toAccount() {
        return Account.create(name, balance, balance, 1);
    }
    
}
