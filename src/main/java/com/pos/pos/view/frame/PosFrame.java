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
import lombok.SneakyThrows;
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

    private final Register register;


    @Getter
    private transient List<LineItem> lineItemList = new ArrayList<>();
    private DefaultListModel<String> listModel;

    private final JButton  totalBtn = new JButton("Total");
    private final JButton  voidItemBtn = new JButton("Void Item");
    private final JButton voidBasketBtn = new JButton("Void Basket");
//    private final JButton cashBtn = new JButton("Cash");
//    private final JButton creditBtn = new JButton("Credit");
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

        if(event.getAction() == RegisterEventEnums.DISCOUNTAPPLIED ) {
            displayDiscountAndTotal();
        }

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal subtotal = BigDecimal.ZERO;
        if (event.getBasket() == null || event.getBasket().getNonVoidedLineItems() == null ) {
            lineItemList = new ArrayList<>();
        } else {
            lineItemList = new ArrayList<>(event.getBasket().getNonVoidedLineItems());
            total = event.getBasket().getTotal();
            subtotal = event.getBasket().getSubtotal();
        }
        updateLineItemList();
        updateTotals(total, subtotal);
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

    private void initComponent() {
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


        totalBtn.setBounds(400,700, 150,150);
        voidItemBtn.setBounds(550,700, 150,150);
        voidBasketBtn.setBounds(700,700, 150,150);

        basketHeader.setBounds(20,20,150,50);
        basketChart.setBounds(20,65,200,25);
        subTotal.setBounds(20,710,150,50);
        total.setBounds(20,750,150,50);
        subTotalValue.setBounds(120,710,150,50);
        totalValue.setBounds(120,750,150,50);


        add(totalBtn);
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
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });

        totalBtn.addActionListener(this::clickTotalBtn);
        voidItemBtn.addActionListener(this::clickVoidItemBtn);
        voidBasketBtn.addActionListener(this::clickVoidBasketBtn);
    }

    @SneakyThrows
    public void clickTotalBtn(ActionEvent e) {
        if(basketEmptyCheck()){return;}
        register.getDiscountForCheckout();
    }
    private void clickVoidItemBtn(ActionEvent e) {
        if(basketEmptyCheck()){return;}
        register.itemVoided();
    }
    private void clickVoidBasketBtn(ActionEvent e) {
        if(basketEmptyCheck()){return;}
        register.voidBasket();
        lineItemList = new ArrayList<>();
        this.updateLineItemList();
        updateTotals(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private void addItemsToGrid(JPanel itemGrid) {
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

        private void displayDiscountAndTotal() {
            JPanel panel = new JPanel(null);
            JDialog totalDialog = new JDialog(this, "Basket Total", true);

            JLabel discountLabel = new JLabel("<html><span style='font-size:20px'>Customer received a discount of $"
                    + register.getOrCreateBasket().getDiscount() + "</span></html>" );
            JLabel totalLabel = new JLabel("<html><span style='font-size:20px'>Amount Due: $"
                    + register.getOrCreateBasket().getTotal() + "</span></html>");

            JButton cashBtn = new JButton("Cash");
            JButton creditBtn = new JButton("Credit");

            discountLabel.setBounds(75,75,600,50);
            totalLabel.setBounds(185,125,600,25);

            creditBtn.setBounds(125,225, 200,200);
            cashBtn.setBounds(375,225, 200,200);


            panel.add(discountLabel);
            panel.add(totalLabel);
            panel.add(cashBtn);
            panel.add(creditBtn);

            totalDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            totalDialog.getContentPane().add(panel);
            totalDialog.setSize(700, 500);
            totalDialog.setLocationRelativeTo(this);

            creditBtn.addActionListener(e -> {
                        register.checkout(RegisterEventEnums.CREDITCHECKOUT);
                        lineItemList = new ArrayList<>();
                        this.updateLineItemList();
                        this.updateTotals(BigDecimal.ZERO, BigDecimal.ZERO);
                        totalDialog.dispose();
                    });

            cashBtn.addActionListener(e -> {
                register.checkout(RegisterEventEnums.CASHCHECKOUT);
                lineItemList = new ArrayList<>();
                this.updateLineItemList();
                this.updateTotals(BigDecimal.ZERO, BigDecimal.ZERO);
                totalDialog.dispose();
            });

            totalDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    register.endBasket();
                }
            });

            totalDialog.setVisible(true);
        }

    private void updateLineItemList() {
        listModel.removeAllElements();
        for (LineItem item : lineItemList) {
            listModel.addElement(item.getItem().getName() + "\t\t\t 1 \t\t\t\t"+ item.getItem().getPrice().toString());
        }
    }

    private void updateTotals(BigDecimal total, BigDecimal subtotal) {
        totalValue.setText("<html><span style='font-size:16px'>$"+ decimalFormat.format(total) +"</span></html>");
        subTotalValue.setText("<html><span style='font-size:16px'>$"+ decimalFormat.format(subtotal)+"</span></html>");
    }

    private boolean basketEmptyCheck() {
        if(lineItemList.isEmpty()){
            JOptionPane.showMessageDialog(null, "Basket is empty.");
            return true;
        }
        return false;
    }
}
