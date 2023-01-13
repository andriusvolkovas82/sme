package sme.account.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sme.account.model.AccountStatus;
import sme.account.model.request.TransferOperationRequest;
import sme.account.model.response.AccountBalanceResponse;
import sme.account.service.AccountService;

import static sme.account.controller.Path.ACCOUNT;
import static sme.account.controller.Path.BALANCE;
import static sme.account.controller.Path.STATUS;
import static sme.account.controller.Path.TRANSFER;

@Tag(name = "Account operations")
@RequestMapping(ACCOUNT)
@RestController
public class AccountController {

    private final AccountService accountService;

    protected static final String OPERATION_COMPLETED = "Operation completed";

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(STATUS + "/{accountNumber}")
    public ResponseEntity<AccountStatus> getAccountStatus(@PathVariable Long accountNumber) {
        return ResponseEntity.ok(accountService.getStatus(accountNumber));
    }

    @GetMapping(BALANCE + "/{accountNumber}")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(@PathVariable Long accountNumber) {
        return ResponseEntity.ok(accountService.balanceInquiry(accountNumber));
    }

    @PostMapping(TRANSFER)
    public ResponseEntity<String> transfer(@RequestBody TransferOperationRequest request) {
        accountService.transfer(request);
        return ResponseEntity.ok(OPERATION_COMPLETED);
    }

}
