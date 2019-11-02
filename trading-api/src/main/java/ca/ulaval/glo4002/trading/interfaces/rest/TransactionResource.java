package ca.ulaval.glo4002.trading.interfaces.rest;

import ca.ulaval.glo4002.trading.application.DTO.BuyTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.SellTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.service.TransactionService;
import ca.ulaval.glo4002.trading.application.service.exceptions.UnsupportedTransactionTypeException;
import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;


@Path("/accounts")
public class TransactionResource {

    private final TransactionService transactionService;

    @Inject
    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @POST
    @Path("{accountNumber}/transactions")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response performTransaction(@PathParam("accountNumber") String accountNumber,
                                       TransactionRequestDTO transactionRequestDTO) {
        try {
            if (transactionRequestDTO.type.toUpperCase().equals("BUY")) {
                BuyTransactionResponseDTO buyTransactionResponseDTO = this.transactionService
                        .createBuyTransaction(transactionRequestDTO, accountNumber);

                URI uri = URI.create("/accounts/" + accountNumber + "/transactions/"
                        + buyTransactionResponseDTO.transactionNumber);
                return Response.created(uri).build();

            } else if (transactionRequestDTO.type.toUpperCase().equals("SELL")) {
                SellTransactionResponseDTO sellTransactionResponseDTO = this.transactionService
                        .createSellTransaction(transactionRequestDTO, accountNumber);

                URI uri = URI.create("/accounts/" + accountNumber + "/transactions/"
                        + sellTransactionResponseDTO.transactionNumber);
                return Response.created(uri).build();

            } else {
                throw new UnsupportedTransactionTypeException(transactionRequestDTO.type);
            }
        } catch (TradingAPIException e) {
            return Response.status(e.getErrorCode()).entity(e.generateErrorMessage()).build();
        }
    }

    @GET
    @Path("{accountNumber}/transactions/{transactionNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response viewTransactionDetails(@PathParam("accountNumber") String accountNumber,
                                           @PathParam("transactionNumber") String transactionNumber) {
        try {
            TransactionResponseDTO transactionResponseDTO = transactionService.getTransactionResponseDTO(accountNumber,
                    transactionNumber);
            return Response.status(Response.Status.OK).entity(transactionResponseDTO).build();
        } catch (TradingAPIException e) {
            return Response.status(e.getErrorCode()).entity(e.generateErrorMessage()).build();
        }
    }
}
