package com.pos.pos;

import com.pos.pos.controllers.BarcodeScanner;
import com.pos.pos.controllers.Register;
import com.pos.pos.models.Basket;
import com.pos.pos.models.LineItem;
import com.pos.pos.models.Item;
import com.pos.pos.services.PriceBookService;
import com.pos.pos.services.VirtualJournalService;
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
    private VirtualJournalService virtualJournalService;

    @Mock
    private BarcodeScanner barcodeScanner;

    @InjectMocks
    private Register register;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        register = new Register(priceBookService, barcodeScanner);
    }

    @Test
    void startBasketShouldInitializeBasketAndNotifyVirtualJournal() {
        // Call the method
        register.startBasket();

        Assertions.assertThat(register.getBasket()).isNotNull();
        Assertions.assertThat(register.getBasket().getLineItems()).isNotNull();
        Mockito.verify(virtualJournalService, times(3)).basketInitialized();
    }

    @Test
    void itemAddedShouldAppendLineItemToBasketAndNotifyVirtualJournal() {
        // Mock data
        LineItem lineItem = new LineItem(new Item(1,"item",BigDecimal.TEN), BigDecimal.TEN, 1, false);

        when(priceBookService.getItem(Mockito.anyLong())).thenReturn(new Item(79,"item", BigDecimal.TEN));

        register.itemAdded(lineItem);

        Assertions.assertThat(register.getBasket().getLineItems()).contains(lineItem);
        Assertions.assertThat(register.getBasket().getSubtotal()).isEqualByComparingTo(BigDecimal.TEN);
        Assertions.assertThat(register.getBasket().getTotal()).isEqualByComparingTo(register.getBasket().getSubtotal().add(BigDecimal.TEN.multiply(BigDecimal.valueOf(.07))));
        Mockito.verify(virtualJournalService).itemAddedLog(lineItem);
    }

//    @Test
    void itemVoidedShouldVoidLineItemInBasketAndNotifyVirtualJournal() {
        // Mock data
        LineItem lineItem = new LineItem(new Item(1,"item",BigDecimal.TEN), BigDecimal.TEN, 1, false);

        register.itemAdded(lineItem);
//        register.itemVoided(lineItem);

        Assertions.assertThat(register.getBasket().getLineItems().get(0).isVoided());
        Assertions.assertThat(register.getBasket().getSubtotal()).isEqualByComparingTo(String.valueOf(0));
        Assertions.assertThat(register.getBasket().getTotal()).isEqualByComparingTo(String.valueOf(0));
        Mockito.verify(virtualJournalService, times(1)).itemVoidedLog(lineItem);
    }

    @Test
    void basketVoidedShouldVoidBasketAndNotifyVirtualJournal() {
        // Mock data
        register.startBasket();
        Basket basket = register.getBasket();

        register.basketVoided();

        Assertions.assertThat(register.getBasket()).isNull();
        Assertions.assertThat(register.getBasket()).isNotSameAs(basket);
        Mockito.verify(virtualJournalService).basketVoidedLog(basket);
    }

    @Test
    void endBasketShouldCompleteBasketAndNotifyVirtualJournal() {
        // Mock data
        register.startBasket();
        Basket basket = register.getBasket();

        register.endBasket();

        Assertions.assertThat(register.getBasket()).isNull();
        Assertions.assertThat(register.getBasket()).isNotSameAs(basket);
        Mockito.verify(virtualJournalService).basketComplete(basket);
    }

    @Test
    void scannedItemShouldReturnCorrectLineItem() {
        // Mock data
        when(priceBookService.getItem(Mockito.anyLong())).thenReturn(new Item(79,"TestItem", BigDecimal.TEN));

        LineItem lineItem = register.scannedItem("79");

        Assertions.assertThat(lineItem.getItem().getName()).isEqualTo("TestItem");
        Assertions.assertThat(lineItem.getQuantity()).isEqualTo(1);
        Assertions.assertThat(lineItem.getPrice()).isEqualTo(BigDecimal.TEN);
        Assertions.assertThat(lineItem.isVoided()).isFalse();
    }

    @Test
    void onScannedShouldHandleScannedData() {
        // Mock data
        when(priceBookService.getItem(Mockito.anyLong())).thenReturn(new Item(79,"TestItem", BigDecimal.TEN));

        register.onScanned("79");

        Mockito.verify(virtualJournalService).itemAddedLog(Mockito.any(LineItem.class));
    }

    @Test
    void sendPriceBookShouldReturnPriceBookFromService() {
        // Mock data
        List<Item> itemList = new ArrayList<>();
        when(priceBookService.getPriceBook()).thenReturn(itemList);

        List<Item> result = register.sendPriceBook();

        Assertions.assertThat(result).isSameAs(itemList);
    }
}

