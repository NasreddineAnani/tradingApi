package ca.ulaval.glo4002.trading.interfaces.rest;

import ca.ulaval.glo4002.trading.application.DTO.BuyTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.SellTransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionRequestDTO;
import ca.ulaval.glo4002.trading.application.DTO.TransactionResponseDTO;
import ca.ulaval.glo4002.trading.application.service.TransactionService;
import ca.ulaval.glo4002.trading.application.service.exceptions.AccountNotFoundException;
import ca.ulaval.glo4002.trading.application.service.exceptions.StockDateNotFoundException;
import ca.ulaval.glo4002.trading.application.service.exceptions.TransactionNotFoundException;
import ca.ulaval.glo4002.trading.domain.transaction.BuyTransaction;
import ca.ulaval.glo4002.trading.domain.transaction.SellTransaction;
import ca.ulaval.glo4002.trading.domain.stock.Stock;
import ca.ulaval.glo4002.trading.domain.transaction.Transaction;
import ca.ulaval.glo4002.trading.domain.exceptions.TradingAPIException;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class TransactionResourceTest {

    private final static long A_POSITIVE_QUANTITY = 10L;
    private final static String A_MARKET = "NASDAQ";
    private final static String A_SYMBOL = "GOOG";
    private final static String DTO_TYPE_BUY = "BUY";
    private final static String DTO_TYPE_SELL = "SELL";
    private final static ZonedDateTime A_DATE = ZonedDateTime.now();
    private final static String AN_ACCOUNT_NUMBER = "anyAccountNumber";
    private final static String A_TRANSACTION_NUMBER = "anyTransactionNumber";

    private TransactionResource aTransactionResource;
    private TransactionService aTransactionServiceMock;
    private Stock aValidStock;
    private BuyTransaction aValidBuyTransaction;

    @Before
    public void setUp() {
        aValidStock = new Stock(A_SYMBOL, A_MARKET);
        aTransactionServiceMock = mock(TransactionService.class);
        aTransactionResource = new TransactionResource(aTransactionServiceMock);
        aValidBuyTransaction = new BuyTransaction(ZonedDateTime.now(), aValidStock, A_POSITIVE_QUANTITY);
    }

    @Test
    public void givenAValidSellTypeTransaction_whenPerformingTransaction_thenASellTransactionIsCreated() {
        SellTransaction aValidSellTransaction = new SellTransaction
                (A_DATE, aValidStock, A_POSITIVE_QUANTITY, aValidBuyTransaction.getTransactionNumber());
        TransactionRequestDTO aTransactionRequestDTO = fromTransactionToTransactionRequestDTO(aValidSellTransaction);
        aTransactionRequestDTO.type = DTO_TYPE_SELL;
        when(aTransactionServiceMock.createSellTransaction(aTransactionRequestDTO, aValidSellTransaction
                .getTransactionNumber())).thenReturn(new SellTransactionResponseDTO());

        aTransactionResource.performTransaction(aValidSellTransaction.getTransactionNumber(), aTransactionRequestDTO);

        verify(aTransactionServiceMock).createSellTransaction(aTransactionRequestDTO, aValidSellTransaction
                .getTransactionNumber());
    }

    @Test
    public void givenAValidBuyTypeTransaction_whenPerformingTransaction_thenABuyTransactionIsCreated() {
        BuyTransaction aValidBuyTransaction = new BuyTransaction
                (A_DATE, aValidStock, A_POSITIVE_QUANTITY);
        TransactionRequestDTO aTransactionRequestDTO = fromTransactionToTransactionRequestDTO(aValidBuyTransaction);
        when(aTransactionServiceMock.createBuyTransaction(aTransactionRequestDTO, aValidBuyTransaction
                .getTransactionNumber())).thenReturn(new BuyTransactionResponseDTO());

        aTransactionResource.performTransaction(aValidBuyTransaction.getTransactionNumber(), aTransactionRequestDTO);

        verify(aTransactionServiceMock).createBuyTransaction(aTransactionRequestDTO, aValidBuyTransaction
                .getTransactionNumber());
    }

    @Test
    public void givenATransactionWithAnInvalidType_whenPerformingTransaction_thenReturnBadRequest() {
        TransactionRequestDTO aTransactionRequestDTO = new TransactionRequestDTO();
        aTransactionRequestDTO.type = "invalid_type";

        Response response = aTransactionResource.performTransaction(AN_ACCOUNT_NUMBER, aTransactionRequestDTO);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenAnInvalidTransactionThatThrowsAnException_whenPerformingTransaction_thenTheErrorMessageFromTheExceptionIsReturned()
            throws TradingAPIException {
        BuyTransaction aBuyTransaction = new BuyTransaction(ZonedDateTime.now(), aValidStock, A_POSITIVE_QUANTITY);
        TradingAPIException thrownException = new StockDateNotFoundException();
        TransactionRequestDTO aTransactionRequestDTO = fromTransactionToTransactionRequestDTO(aBuyTransaction);
        doThrow(thrownException).when(aTransactionServiceMock).createBuyTransaction(aTransactionRequestDTO,
                aBuyTransaction.getTransactionNumber());

        Response response = aTransactionResource.performTransaction(aBuyTransaction.getTransactionNumber(),
                aTransactionRequestDTO);

        assertEquals(thrownException.generateErrorMessage(), response.getEntity());
        assertEquals(thrownException.getErrorCode(), response.getStatus());
    }

    @Test
    public void givenAnExistingTransactionNumberAssociatedWithAnExistingAccountNumber_whenILookForTransactionDetails_thenIReceiveAnOKStatusCodeAndTheTransactionResponse() {
        TransactionResponseDTO transactionRequestDTO = new TransactionResponseDTO();
        when(aTransactionServiceMock.getTransactionResponseDTO(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER))
                .thenReturn(transactionRequestDTO);

        Response response = aTransactionResource.viewTransactionDetails(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(transactionRequestDTO, response.getEntity());
    }


    @Test
    public void givenAnInvalidAccountNumber_whenILookForTransactionDetails_thenIReceiveANotFoundStatusCodeAndTheAccountNotFoundMessage() {
        AccountNotFoundException thrownException = new AccountNotFoundException(AN_ACCOUNT_NUMBER);
        doThrow(thrownException).when(aTransactionServiceMock)
                .getTransactionResponseDTO(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER);

        Response response = aTransactionResource.viewTransactionDetails(AN_ACCOUNT_NUMBER,
                A_TRANSACTION_NUMBER);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(thrownException.generateErrorMessage(), response.getEntity());
    }

    @Test
    public void givenAnInvalidTransactionNumber_whenILookForTransactionDetails_thenIReceiveANotFoundStatusCodeAndTheTransactionNotFoundMessage() {
        TransactionNotFoundException thrownException = new TransactionNotFoundException(A_TRANSACTION_NUMBER);
        doThrow(thrownException).when(aTransactionServiceMock)
                .getTransactionResponseDTO(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER);

        Response response = aTransactionResource.viewTransactionDetails(AN_ACCOUNT_NUMBER, A_TRANSACTION_NUMBER);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(thrownException.generateErrorMessage(), response.getEntity());
    }

    private TransactionRequestDTO fromTransactionToTransactionRequestDTO(Transaction transaction) {

        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.type = DTO_TYPE_BUY;
        transactionRequestDTO.date = transaction.getDate().toString();
        transactionRequestDTO.quantity = transaction.getQuantity();
        transactionRequestDTO.stock = transaction.getStock();
        transactionRequestDTO.transactionNumber = transaction.getTransactionNumber();

        return transactionRequestDTO;
    }
}