package com.pos.pos;

import com.pos.pos.controllers.BarcodeScanner;
import com.pos.pos.controllers.Register;
import com.pos.pos.models.Basket;
import com.pos.pos.models.LineItem;
import com.pos.pos.models.PriceBook;
import com.pos.pos.service.PriceBookService;
import com.pos.pos.service.VirtualJournal;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class RegisterTests {

    @Mock
    private PriceBookService priceBookService;

    @Mock
    private VirtualJournal virtualJournal;

    @Mock
    private BarcodeScanner barcodeScanner;

    @InjectMocks
    private Register register;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        register = new Register(priceBookService, virtualJournal, barcodeScanner);
    }

    @Test
    void startBasketShouldInitializeBasketAndNotifyVirtualJournal() {
        // Call the method
        register.startBasket();

        Assertions.assertThat(register.getBasket()).isNotNull();
        Assertions.assertThat(register.getBasket().getLineItems()).isNotNull();
        Mockito.verify(virtualJournal, times(3)).basketInitialized();
    }

    @Test
    void itemAddedShouldAppendLineItemToBasketAndNotifyVirtualJournal() {
        // Mock data
        LineItem lineItem = new LineItem("TestItem", BigDecimal.TEN, 1, false);

        when(priceBookService.getPriceBookItem(Mockito.anyLong())).thenReturn(new PriceBook(79,"TestItem", BigDecimal.TEN));

        boolean result = register.itemAdded(lineItem);

        Assertions.assertThat(result).isTrue();
        Assertions.assertThat(register.getBasket().getLineItems()).contains(lineItem);
        Assertions.assertThat(register.getBasket().getSubtotal()).isEqualByComparingTo(BigDecimal.TEN);
        Assertions.assertThat(register.getBasket().getTotal()).isEqualByComparingTo(register.getBasket().getSubtotal().add(BigDecimal.TEN.multiply(BigDecimal.valueOf(.07))));
        Mockito.verify(virtualJournal).itemAddedLog(lineItem);
    }

    @Test
    void itemVoidedShouldVoidLineItemInBasketAndNotifyVirtualJournal() {
        // Mock data
        LineItem lineItem = new LineItem("TestItem", BigDecimal.TEN, 1, false);

        register.itemAdded(lineItem);
        register.itemVoided(lineItem);

        Assertions.assertThat(register.getBasket().getLineItems().get(0).isVoided());
        Assertions.assertThat(register.getBasket().getSubtotal()).isEqualByComparingTo(String.valueOf(0));
        Assertions.assertThat(register.getBasket().getTotal()).isEqualByComparingTo(String.valueOf(0));
        Mockito.verify(virtualJournal, times(1)).itemVoidedLog(lineItem);
    }

    @Test
    void basketVoidedShouldVoidBasketAndNotifyVirtualJournal() {
        // Mock data
        register.startBasket();
        Basket basket = register.getBasket();

        register.basketVoided();

        Assertions.assertThat(register.getBasket()).isNull();
        Assertions.assertThat(register.getBasket()).isNotSameAs(basket);
        Mockito.verify(virtualJournal).basketVoidedLog(basket);
    }

    @Test
    void endBasketShouldCompleteBasketAndNotifyVirtualJournal() {
        // Mock data
        register.startBasket();
        Basket basket = register.getBasket();

        register.endBasket();

        Assertions.assertThat(register.getBasket()).isNull();
        Assertions.assertThat(register.getBasket()).isNotSameAs(basket);
        Mockito.verify(virtualJournal).basketComplete(basket);
    }

    @Test
    void scannedItemShouldReturnCorrectLineItem() {
        // Mock data
        when(priceBookService.getPriceBookItem(Mockito.anyLong())).thenReturn(new PriceBook(79,"TestItem", BigDecimal.TEN));

        LineItem lineItem = register.scannedItem("79");

        Assertions.assertThat(lineItem.getName()).isEqualTo("TestItem");
        Assertions.assertThat(lineItem.getQuantity()).isEqualTo(1);
        Assertions.assertThat(lineItem.getValue()).isEqualTo(BigDecimal.TEN);
        Assertions.assertThat(lineItem.isVoided()).isFalse();
    }

    @Test
    void onScannedShouldHandleScannedData() {
        // Mock data
        when(priceBookService.getPriceBookItem(Mockito.anyLong())).thenReturn(new PriceBook(79,"TestItem", BigDecimal.TEN));

        register.onScanned("79");

        Mockito.verify(virtualJournal).itemAddedLog(Mockito.any(LineItem.class));
    }

    @Test
    void sendPriceBookShouldReturnPriceBookFromService() {
        // Mock data
        List<PriceBook> priceBookList = new ArrayList<>();
        when(priceBookService.getPriceBook()).thenReturn(priceBookList);

        List<PriceBook> result = register.sendPriceBook();

        Assertions.assertThat(result).isSameAs(priceBookList);
    }
}

