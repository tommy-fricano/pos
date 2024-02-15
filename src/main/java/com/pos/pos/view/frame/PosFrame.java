package com.pos.pos.view.frame;


import com.pos.pos.controllers.Register;
import com.pos.pos.listeners.RegisterEvent;
import com.pos.pos.listeners.RegisterEventEnums;
import com.pos.pos.listeners.RegisterEventListener;
import com.pos.pos.models.Item;
import com.pos.pos.models.LineItem;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PosFrame extends JFrame implements RegisterEventListener {

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    private final transient Register register;

    @Getter
    private transient List<LineItem> lineItemList = new ArrayList<>();
    private DefaultListModel<String> listModel;
    private final JButton  voidItemBtn = new JButton("Void Item");
    private final JButton voidBasketBtn = new JButton("Void Basket");
    private final JButton cashBtn = new JButton("Cash");
    private final JButton creditBtn = new JButton("Credit");
    private final JLabel basketHeader = new JLabel("<html><span style='font-size:22px'>Basket: </span></html>");
    private final JLabel basketChart = new JLabel("<html><span style='font-size:11px'>Item &emsp;&emsp;&emsp;&emsp;Quantity&emsp;Price </span></html>");
    private final JLabel subTotal = new JLabel("<html><span style='font-size:16px'>Subtotal: </span></html>");
    private final JLabel total = new JLabel("<html><span style='font-size:20px'>Total: </span></html>");
    private final JLabel subTotalValue = new JLabel();
    private final JLabel totalValue = new JLabel();

    @PostConstruct
    private void begin(){
        register.addRegisterEventListener(this);
    }


    @Override
    public void updateListeners(RegisterEvent event) {
        lineItemList = event.getBasket().getNonVoidedLineItems() == null ? new ArrayList<>() : new ArrayList<>(event.getBasket().getNonVoidedLineItems());
        updateLineItemList();
        newUpdateTotals(event.getBasket().getTotal(), event.getBasket().getSubtotal());
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
        register.endBasket(RegisterEventEnums.CREDITCHECKOUT);
        lineItemList = new ArrayList<>();
        this.updateLineItemList();
        newUpdateTotals(BigDecimal.ZERO, BigDecimal.ZERO);
        JOptionPane.showMessageDialog(null, "Order Tendered with credit. Starting new basket.");
    }
    private void clickCashBtn(ActionEvent e){
        if(basketEmptyCheck()){return;}
        register.endBasket(RegisterEventEnums.CASHCHECKOUT);
        lineItemList = new ArrayList<>();
        this.updateLineItemList();
        newUpdateTotals(BigDecimal.ZERO, BigDecimal.ZERO);
        JOptionPane.showMessageDialog(null, "Order Tendered with cash. Starting new basket.");
    }
    private void clickVoidItemBtn(ActionEvent e){
        if(basketEmptyCheck()){return;}
        register.itemVoided();
    }
    private void clickVoidBasketBtn(ActionEvent e){
        if(basketEmptyCheck()){return;}
        register.endBasket(RegisterEventEnums.VOIDBASKET);
        lineItemList = new ArrayList<>();
        this.updateLineItemList();
        newUpdateTotals(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private void addItemsToGrid(JPanel itemGrid){
        List<Item> items = register.sendPriceBook();
        for (Item value : items) {
            JButton itemBtn = new JButton("<html><span style='font-size:10px'>" + value.getName() + " </span></html>");
            itemBtn.putClientProperty("item", value);
            itemBtn.setSize(100, 25);
            itemBtn.addActionListener(e -> {
                Item item = (Item) itemBtn.getClientProperty("item");
                LineItem lineItem = LineItem.builder()
                        .item(item)
                        .price(item.getPrice())
                        .quantity(1)
                        .voided(false)
                        .build();

                lineItemList = new ArrayList<>();
                register.itemAdded(lineItem);
            });
            itemGrid.add(itemBtn);
        }
    }

    private void updateLineItemList() {
        listModel.removeAllElements();
        for (LineItem item : lineItemList) {
            listModel.addElement(item.getItem().getName() + "\t\t\t 1 \t\t\t\t"+ item.getItem().getPrice().toString());
        }
    }

    private void newUpdateTotals(BigDecimal total, BigDecimal subtotal){
        totalValue.setText("<html><span style='font-size:16px'>$"+ decimalFormat.format(total) +"</span></html>");
        subTotalValue.setText("<html><span style='font-size:16px'>$"+ decimalFormat.format(subtotal)+"</span></html>");
    }

    private boolean basketEmptyCheck(){
        if(lineItemList.isEmpty()){
            JOptionPane.showMessageDialog(null, "Basket is empty.");
            return true;
        }
        return false;
    }
}
