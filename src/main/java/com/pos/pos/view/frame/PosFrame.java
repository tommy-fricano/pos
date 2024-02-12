package com.pos.pos.view.frame;


import com.pos.pos.controllers.BarcodeScanner;
import com.pos.pos.controllers.Register;
import com.pos.pos.listener.ScannedEventListener;
import com.pos.pos.models.LineItem;
import com.pos.pos.models.PriceBook;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class PosFrame extends JFrame implements ScannedEventListener {

    private static final String COMPLETE = "complete";
    private static final String ADD = "add";
    private static final String VOID = "void";

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    @Autowired
    private final Register register;

    @Autowired
    private final BarcodeScanner barcodeScanner;

    public PosFrame(final Register register, BarcodeScanner barcodeScanner){
        this.register = register;
        this.barcodeScanner = barcodeScanner;
    }

    @Getter
    private List<LineItem> lineItemList = new ArrayList<>();
    private DefaultListModel<String> listModel;

    private BigDecimal totalVal = BigDecimal.valueOf(0);
    private BigDecimal subtotalVal = BigDecimal.valueOf(0);
    private JButton  voidItemBtn = new JButton("Void Item");
    private JButton voidBasketBtn = new JButton("Void Basket");
    private JButton cashBtn = new JButton("Cash");
    private JButton creditBtn = new JButton("Credit");
    private JLabel basketHeader = new JLabel("<html><span style='font-size:22px'>Basket: </span></html>");
    private JLabel basketChart = new JLabel("<html><span style='font-size:11px'>Item &emsp;&emsp;&emsp;&emsp;Quantity&emsp;Price </span></html>");
    private JLabel subTotal = new JLabel("<html><span style='font-size:16px'>Subtotal: </span></html>");
    private JLabel total = new JLabel("<html><span style='font-size:20px'>Total: </span></html>");
    private JLabel subTotalValue = new JLabel();
    private JLabel totalValue = new JLabel();

    @Override
    public void onScanned(String scannedData) {
        LineItem scannedItem = register.scannedItem(scannedData);
        lineItemList.add(scannedItem);
        updateLineItemList();
        updateTotals(scannedItem, ADD);
    }

    public void setupFrame(){
        setTitle("REGISTER");
        setSize(1000,900);
        setLocation(500,500);
        setLayout(null);
        initComponent();
        initEvent();
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyboardFocusManager.addKeyEventDispatcher(register.getBarcodeScanner());
        barcodeScanner.addScannedEventListener(this);
    }

    private void initComponent(){
        JPanel itemGrid = new JPanel();
        itemGrid.setSize(700,670);
        itemGrid.setLocation(300,25);
        itemGrid.setLayout(new GridLayout(5,6));
        addItemsToGrid(itemGrid);
        add(itemGrid);

        listModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.setBounds(10,90,270,610);
        add(scrollPane, BorderLayout.WEST);


        creditBtn.setBounds(250,700, 150,150);
        cashBtn.setBounds(400,700, 150,150);
        voidItemBtn.setBounds(550,700, 150,150);
        voidBasketBtn.setBounds(700,700, 150,150);

        basketHeader.setBounds(20,20,150,50);
        basketChart.setBounds(20,65,200,25);
        subTotal.setBounds(20,710,150,50);
        total.setBounds(20,750,150,50);
        subTotalValue.setBounds(120,710,150,50);
        totalValue.setBounds(120,750,150,50);

        add(creditBtn);
        add(cashBtn);
        add(voidItemBtn);
        add(voidBasketBtn);


        add(subTotalValue);
        add(totalValue);
        add(basketHeader);
        add(basketChart);
        add(subTotal);
        add(total);
    }

    private void initEvent(){
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                System.exit(1);
            }
        });

        creditBtn.addActionListener(this::clickCreditBtn);
        cashBtn.addActionListener(this::clickCashBtn);
        voidItemBtn.addActionListener(this::clickVoidItemBtn);
        voidBasketBtn.addActionListener(this::clickVoidBasketBtn);
    }


    public void clickCreditBtn(ActionEvent e){
        if(basketEmptyCheck()){return;}
        listModel.removeAllElements();
        lineItemList = null;
        register.endBasket();
        lineItemList = new ArrayList<>();
        updateTotals(null, COMPLETE);
        JOptionPane.showMessageDialog(null, "Order Tendered with credit. Starting new basket.");

    }
    private void clickCashBtn(ActionEvent e){
        if(basketEmptyCheck()){return;}
        listModel.removeAllElements();
        lineItemList = null;
        register.endBasket();
        lineItemList = new ArrayList<>();
        updateTotals(null, COMPLETE);
        JOptionPane.showMessageDialog(null, "Order Tendered with cash. Starting new basket.");
    }
    private void clickVoidItemBtn(ActionEvent e){
        if(basketEmptyCheck()){return;}
        LineItem last = lineItemList.get(lineItemList.size()-1);
        if(last.getQuantity() == 1){
            lineItemList.remove(last);
        }
        register.itemVoided(last);
        updateTotals(last, VOID);
        updateLineItemList();
    }
    private void clickVoidBasketBtn(ActionEvent e){
        if(basketEmptyCheck()){return;}
        listModel.removeAllElements();
        lineItemList = null;
        register.basketVoided();
        lineItemList = new ArrayList<>();
        updateTotals(null, COMPLETE);
    }

    private void addItemsToGrid(JPanel itemGrid){
        List<PriceBook> items = register.sendPriceBook();
        for(int i =0 ; i < items.size(); i++){
            JButton itemBtn = new JButton("<html><span style='font-size:10px'>"+items.get(i).getItemName()+" </span></html>");
            itemBtn.putClientProperty("item", items.get(i));
            itemBtn.setSize(100,25);
            itemBtn.addActionListener(e -> {
                PriceBook item = (PriceBook) itemBtn.getClientProperty("item");
                LineItem lineItem = LineItem.builder()
                        .name(item.getItemName())
                        .value(item.getPrice())
                        .quantity(1)
                        .voided(false)
                        .build();

                LineItem itemForRegister = LineItem.builder()
                        .name(item.getItemName())
                        .value(item.getPrice())
                        .quantity(1)
                        .voided(false)
                        .build();

                lineItemList.add(lineItem);
                updateLineItemList();
                updateTotals(lineItem, ADD);
                register.itemAdded(itemForRegister);
            });
            itemGrid.add(itemBtn);
        }
    }

    private void updateLineItemList() {
        listModel.removeAllElements();
        for (LineItem item : lineItemList) {
            listModel.addElement(item.getName() + "\t\t\t 1 \t\t\t\t"+ item.getValue().toString());
        }
    }

    private void updateTotals(LineItem lineItem, String operation){
        switch (operation) {
            case ADD -> {
                subtotalVal = subtotalVal.add(lineItem.getValue());
                totalVal = subtotalVal.add(subtotalVal.multiply(BigDecimal.valueOf(.07)));
            }
            case VOID -> {
                subtotalVal = subtotalVal.subtract(lineItem.getValue());
                totalVal = subtotalVal.add(subtotalVal.multiply(BigDecimal.valueOf(.07)));
            }
            case COMPLETE -> {
                subtotalVal = BigDecimal.valueOf(0);
                totalVal = BigDecimal.valueOf(0);
            }
        }
        totalValue.setText("<html><span style='font-size:16px'>$"+ decimalFormat.format(totalVal) +"</span></html>");
        subTotalValue.setText("<html><span style='font-size:16px'>$"+ decimalFormat.format(subtotalVal)+"</span></html>");
    }

    private boolean basketEmptyCheck(){
        if(lineItemList.isEmpty()){
            JOptionPane.showMessageDialog(null, "Basket is empty.");
            return true;
        }
        return false;
    }
}
