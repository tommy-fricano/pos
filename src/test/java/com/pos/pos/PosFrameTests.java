package com.pos.pos;

import com.pos.pos.controllers.BarcodeScanner;
import com.pos.pos.controllers.Register;
import com.pos.pos.models.LineItem;
import com.pos.pos.models.Item;
import com.pos.pos.view.frame.PosFrame;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;


public class PosFrameTests {

        private Register register;
        private BarcodeScanner barcodeScanner;


        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            register = Mockito.mock(Register.class);
            barcodeScanner = Mockito.mock(BarcodeScanner.class);
        }

//        @Test
        void onScannedShouldAddLineItemAndUpdateLists() {
            PosFrame posFrame = new PosFrame(register);
            posFrame.setupFrame();
            posFrame.setVisible(false);

            // Mock data
            Item item = new Item(79,"item", BigDecimal.TEN);
            LineItem lineItem = new LineItem(new Item(79,"item",BigDecimal.TEN), BigDecimal.TEN, 1, false);

            Mockito.when(register.scannedItem(Mockito.anyString())).thenReturn(lineItem);

            posFrame.onScanned("79");

            Mockito.verify(register).scannedItem("79");

            Assertions.assertThat(posFrame.getLineItemList()).contains(lineItem);

        }

//        @Test
        void clickCreditBtnShouldEndBasketAndShowMessage() {
//            PosFrame posFrame = new PosFrame(register, barcodeScanner);
            posFrame.setupFrame();
            posFrame.setVisible(false);


            Mockito.doNothing().when(register).endBasket();

            posFrame.clickCreditBtn(null);

            Mockito.verify(register).endBasket();

        }
 }
