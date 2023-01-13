package sme.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import sme.account.model.AccountStatus;
import sme.account.model.request.TransferOperationRequest;
import sme.account.model.response.AccountBalanceResponse;
import sme.account.repository.AccountRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sme.account.controller.AccountController.OPERATION_COMPLETED;
import static sme.account.controller.Path.ACCOUNT;
import static sme.account.controller.Path.BALANCE;
import static sme.account.controller.Path.STATUS;
import static sme.account.controller.Path.TRANSFER;
import static sme.account.exception.ErrorMessage.ACCOUNT_IS_CLOSED;
import static sme.account.exception.ErrorMessage.ACCOUNT_NOT_FOUND;
import static sme.account.exception.ErrorMessage.INSUFFICIENT_FUNDS;
import static sme.account.exception.ErrorMessage.INVALID_AMOUNT;
import static sme.account.exception.ErrorMessage.INVALID_CURRENCY;
import static sme.account.model.AccountOperation.CREDIT;
import static sme.account.model.AccountOperation.DEBIT;
import static sme.account.model.AccountStatus.CLOSED;
import static sme.account.model.AccountStatus.OPEN;
import static sme.account.util.CurrencyHelper.findCurrencyByNumericCode;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @Sql({"classpath:/import.sql"})
    void getAccountBalance_whenNotFoundAccount_thenReturnError() throws Exception {
        String actualResponse = mockMvc.perform(get(ACCOUNT + BALANCE + "/123"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(ACCOUNT_NOT_FOUND.getMessage(), actualResponse);
    }

    @Test
    @Sql({"classpath:/import.sql"})
    void getAccountStatus() throws Exception {
        String actualResponse = mockMvc.perform(get(ACCOUNT + STATUS + "/1000123"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = OPEN.toString();

        assertEquals(toJson(expectedResponse), actualResponse);
    }

    @Test
    @Sql({"classpath:/import.sql"})
    void getAccountBalance() throws Exception {
        String actualResponse = mockMvc.perform(get(ACCOUNT + BALANCE + "/1000236"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AccountBalanceResponse expectedResponse = new AccountBalanceResponse(1000236L, findCurrencyByNumericCode(978), new BigDecimal("0.00"), CLOSED);

        assertEquals(toJson(expectedResponse), actualResponse);
    }

    @Test
    @Sql({"classpath:/import.sql"})
    void transferDebit() throws Exception {
        String actualResponse = mockMvc.perform(get(ACCOUNT + BALANCE + "/1000456"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AccountBalanceResponse expectedResponse = new AccountBalanceResponse(1000456L, findCurrencyByNumericCode(840), new BigDecimal("1002.00"), OPEN);

        assertEquals(toJson(expectedResponse), actualResponse);


        TransferOperationRequest request = new TransferOperationRequest(1000456L, new BigDecimal("1.00"), 840, DEBIT);
        actualResponse = mockMvc.perform(post(ACCOUNT + TRANSFER)
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(OPERATION_COMPLETED, actualResponse);


        actualResponse = mockMvc.perform(get(ACCOUNT + BALANCE + "/1000456"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        expectedResponse = new AccountBalanceResponse(1000456L, findCurrencyByNumericCode(840), new BigDecimal("1003.00"), OPEN);

        assertEquals(toJson(expectedResponse), actualResponse);
    }

    @Test
    @Sql({"classpath:/import.sql"})
    void transferCredit() throws Exception {
        TransferOperationRequest request = new TransferOperationRequest(1000456L, new BigDecimal("1.00"), 840, CREDIT);
        String actualResponse = mockMvc.perform(post(ACCOUNT + TRANSFER)
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(OPERATION_COMPLETED, actualResponse);
    }

    @Test
    @Sql({"classpath:/import.sql"})
    void transfer_whenValidationFails_thenReturnError() throws Exception {
        TransferOperationRequest request = new TransferOperationRequest(1000236L, new BigDecimal("1.00"), 840, DEBIT);
        String actualResponse = mockMvc.perform(post(ACCOUNT + TRANSFER)
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(ACCOUNT_IS_CLOSED.getMessage(), actualResponse);


        request = new TransferOperationRequest(1000123L, new BigDecimal("1.00"), 840, DEBIT);
        actualResponse = mockMvc.perform(post(ACCOUNT + TRANSFER)
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(INVALID_CURRENCY.getMessage(), actualResponse);


        request = new TransferOperationRequest(1000123L, new BigDecimal("-1.00"), 978, DEBIT);
        actualResponse = mockMvc.perform(post(ACCOUNT + TRANSFER)
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(INVALID_AMOUNT.getMessage(), actualResponse);


        request = new TransferOperationRequest(1000456L, new BigDecimal("1003.00"), 840, CREDIT);
        actualResponse = mockMvc.perform(post(ACCOUNT + TRANSFER)
                .contentType(APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(INSUFFICIENT_FUNDS.getMessage(), actualResponse);
    }

    private static String toJson(Object obj) throws JsonProcessingException {
            return new ObjectMapper().writeValueAsString(obj);
    }

}
