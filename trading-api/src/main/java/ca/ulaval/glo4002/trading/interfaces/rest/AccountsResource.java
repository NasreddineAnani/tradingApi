package ca.ulaval.glo4002.trading.interfaces.rest;

import ca.ulaval.glo4002.trading.application.DTO.AccountRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.AccountResponseDTO;
import ca.ulaval.glo4002.trading.application.assembler.AccountAssembler;
import ca.ulaval.glo4002.trading.application.service.AccountService;
import ca.ulaval.glo4002.trading.application.service.exceptions.AccountNotFoundException;
import ca.ulaval.glo4002.trading.domain.account.Account;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountAlreadyOpenException;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidCreditsException;
import ca.ulaval.glo4002.trading.domain.exceptions.AccountInvalidDetailsException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;


@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {
    private final AccountService accountService;
    private final AccountAssembler accountAssembler;

    @Inject
    public AccountsResource(AccountService accountService, AccountAssembler accountAssembler) {
        this.accountService = accountService;
        this.accountAssembler = accountAssembler;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postAccount(AccountRequestDTO accountRequestDTO) {
        try {
            String accountNumber = this.accountService.createAccount(accountRequestDTO);
            URI uri = URI.create(String.format("/accounts/%s", accountNumber));
            return Response.created(uri).build();
        } catch (AccountInvalidDetailsException | AccountInvalidCreditsException
                | AccountAlreadyOpenException exception) {
            return Response.status(exception.getErrorCode()).entity(exception.generateErrorMessage()).build();
        }
    }

    @GET
    @Path("/{accountNumber}")
    public Response getAccount(@PathParam("accountNumber") String accountNumber) {
        try {
            Account currentAccount = accountService.findAccountWithAccountNumber(accountNumber);
            AccountResponseDTO accountResponseDTO = accountAssembler.fromAccountToAccountResponseDTO(currentAccount);
            return Response.status(Response.Status.OK).entity(accountResponseDTO).build();
        } catch (AccountNotFoundException exception) {
            return Response.status(exception.getErrorCode()).entity(exception.generateErrorMessage()).build();
        }
    }
}

