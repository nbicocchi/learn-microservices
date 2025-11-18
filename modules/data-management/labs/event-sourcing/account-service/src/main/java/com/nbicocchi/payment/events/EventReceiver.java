package com.nbicocchi.payment.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbicocchi.payment.commands.BankCommand;
import com.nbicocchi.payment.commands.CreateAccount;
import com.nbicocchi.payment.commands.DepositMoney;
import com.nbicocchi.payment.commands.WithdrawMoney;
import com.nbicocchi.payment.persistence.BankAccountEventStore;
import com.nbicocchi.payment.service.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@AllArgsConstructor
@Slf4j
@Component
public class EventReceiver {
    private final EventSender eventSender;
    private final BankAccountEventStore eventStore;
    private final BankAccountService bankAccountService;

    /**
     * Send these to LavinMQ
     * {
     *   "type": "DepositMoney",
     *   "accountId": "acc445",
     *   "amount": 6650.0
     * }
     */
    @Bean
    public Consumer<BankCommand> accountProcessor() {
        return cmd -> {
            log.info("Received command: {}", cmd);

            Event<String, ?> newEvent;

            // Determine command type
            if (cmd instanceof CreateAccount create) {
                newEvent = bankAccountService.handleAccountCreated(create);
            }
            else if (cmd instanceof DepositMoney deposit) {
                newEvent = bankAccountService.handleDeposit(deposit);
            }
            else if (cmd instanceof WithdrawMoney withdraw) {
                newEvent = bankAccountService.handleWithdraw(withdraw);
            }
            else {
                log.warn("Unknown command type: {}", cmd);
                return;
            }

            // Store the resulting event
            eventStore.append(cmd.accountId(), newEvent);

            log.info("Stored event: {}", newEvent);

            // If you want to publish:
            eventSender.send("accountProcessor-out-0", newEvent.getKey(), newEvent);
        };
    }
}
