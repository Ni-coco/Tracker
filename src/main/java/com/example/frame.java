package com.example;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.net.URL;

import java.awt.Image;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
//import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.*;

public class frame extends JFrame implements ActionListener, ChangeListener, FocusListener {

    List<String> crypto = getCrypto();
    List<Double> next = getNext();
    List<ImageIcon> img = getImg();
    JFrame win = new JFrame("Crypto prices"); //frame
    /* Related to Menu */
    JPanel Menu = new JPanel();
    JButton Bmarket = new JButton("Market");
    JButton Btrading = new JButton("Trading");
    /* Related to MarketFrame */
    JPanel MarketFrame = new JPanel();
    JPanel pnPrice = new JPanel();
    JLabel[] coins = new JLabel[6];
    /* Related to TradingFrame */
    JPanel TradingFrame = new JPanel();
    JScrollPane scrollPane = new JScrollPane(rootPane);
    JPanel[] pnTrading = new JPanel[4];
    List<JLabel> order = new ArrayList<JLabel>();
    String[] options = crypto.toArray(new String[0]);
    JComboBox<String> symbol = new JComboBox<String>(options);
    JLabel coin = new JLabel();
    JSlider leverage = new JSlider(1, 50, 1);
    JLabel valueLeverage = new JLabel("x1");
    JTextField amount = new JTextField();
    JButton Longit = new JButton("Long");
    JButton Shortit = new JButton("Short");
    List<JComponent> components = getComp();
    JLabel pnl = new JLabel("PNL");
    Color gay = new Color(60, 63, 65);

    public frame() {

        setMenu();
        setFrame();
        setMarketFrame();

        /* set Panels for TradingFrame */
        TradingFrame.setLayout(new BorderLayout());
        TradingFrame.setBackground(Color.BLACK);
        TradingFrame.setVisible(false);
        pnTrading[0] = new JPanel();
        pnTrading[0].setBackground(Color.BLACK);
        pnTrading[0].setLayout(new BorderLayout());
        Border pn0border = BorderFactory.createMatteBorder(0, 0, 0, 3, Color.LIGHT_GRAY);
        pnTrading[0].setBorder(pn0border);

            /* Order panel */
        pnTrading[1] = new JPanel();
        pnTrading[1].setBackground(Color.BLACK);
        pnTrading[1].setLayout(new GridBagLayout());
        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(20, 0, 0, 0);
        for (int i = 0; i < 50; i++) {
            c.gridy = i;
            pnTrading[1].add(new JLabel("hi"), c);
        }
        JScrollPane scrollPane = new JScrollPane(pnTrading[1]);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setBorder(border);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        pnTrading[0].add(scrollPane, BorderLayout.CENTER);

            /* Resume panel */
        pnTrading[2] = new JPanel();
        pnTrading[2].setBackground(Color.BLACK);
        Border pn2border = BorderFactory.createMatteBorder(3, 0, 0, 0, Color.LIGHT_GRAY);
        pnTrading[2].setBorder(pn2border);
        pnl.setFont(new Font("Arial", Font.PLAIN, 20));
        pnl.setForeground(Color.WHITE);
        pnTrading[2].add(pnl);
        pnTrading[0].add(pnTrading[2], BorderLayout.SOUTH);

            /* Add pn[1&2] to TradingFrame */
        TradingFrame.add(pnTrading[0], BorderLayout.CENTER);

            /* placeOrder panel */
        pnTrading[3] = new JPanel(new GridBagLayout());
        pnTrading[3].setBackground(Color.BLACK);
        c.insets = new Insets(50, 40, 0, 40);
        c.fill = GridBagConstraints.VERTICAL;
        //symbol
        symbol.setForeground(Color.BLACK);
        symbol.setBackground(Color.WHITE);
        symbol.setFocusable(false);
        //coin
        Image scaledImage = img.get(symbol.getSelectedIndex()).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        coin.setIcon(new ImageIcon(scaledImage));
        coin.setText(coins[symbol.getSelectedIndex()].getText());
        //leverage
        leverage.setForeground(gay);
        leverage.setBackground(Color.WHITE);
        //valueLeverage
        valueLeverage.setForeground(Color.WHITE);
        //amount
        amount.setText("Amount in $");
        amount.setForeground(Color.BLACK);
        amount.setBackground(Color.WHITE);
        amount.setMinimumSize(new Dimension(amount.getPreferredSize()));
        amount.setHorizontalAlignment(JTextField.CENTER);
        //long
        Longit.setForeground(Color.BLACK);
        Longit.setBackground(new Color(144, 238, 144));
        //short
        Shortit.setForeground(Color.BLACK);
        Shortit.setBackground(new Color(255, 127, 127));

        for (int i = 0; i < components.size(); i++) {
            c.gridy = i;
            pnTrading[3].add(components.get(i), c);
        }

        TradingFrame.add(pnTrading[3], BorderLayout.EAST);

        symbol.addActionListener(this);
        leverage.addChangeListener(this);
        amount.addFocusListener(this);
        Longit.addActionListener(this);
        Shortit.addActionListener(this);

        /* Visibility and pack for frame */
        win.pack();
        win.setVisible(true);

        for (int i = 0; i < crypto.size(); i++)
            coins[i].setText(next.get(i).toString() + " $");

        for (;;) {
            if (MarketFrame.isVisible())
                getMarket();
            else if (TradingFrame.isVisible())
                getTrading();
        }
    }

    public List<String> getCrypto() {
        List<String> list = new ArrayList<String>();
        list.add("BTC");
        list.add("ETH");
        list.add("BNB");
        list.add("SOL");
        list.add("FTM");
        list.add("WLKN");
        return list;
    }
    

    public List<Double> getNext() {
        List<Double> list = new ArrayList<Double>();
        for (int i = 0; i < crypto.size(); i++) {
            try {
                list.add(Double.parseDouble(getPrices(crypto.get(i))));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return list;
    }

    public List<ImageIcon> getImg() {
        List<ImageIcon> list = new ArrayList<ImageIcon>();
        for (int i = 0; i< crypto.size(); i++)
            list.add(new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("img/"+crypto.get(i)+".png")).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        return list;
    }

    public String getPrices(String str) throws Exception {
        URL financeUrl = new URL("https://min-api.cryptocompare.com/data/price?fsym="+str+"&tsyms=USD&api_key=ef6c349317448a25c217cdab8e57e2c94bb8e053f17c51ca600efcf2ae862a1b");
        InputStreamReader reader = new InputStreamReader(financeUrl.openStream());
        JsonReader jsonReader = new JsonReader(reader);
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(jsonReader).getAsJsonObject();
        return response.get("USD").getAsString();
        //return "100";
    }

    public void getMarket() {
        for (;;) {
            if (!MarketFrame.isVisible())
                break;
            for (int i = 0; i < crypto.size(); i++) {
                try {
                    next.set(i, Double.parseDouble(getPrices(crypto.get(i))));
                    String tmp = coins[i].getText();
                    if (next.get(i) < Double.parseDouble(tmp.replaceAll("[^0-9.]", "")))
                        coins[i].setForeground(Color.RED);
                    else
                        coins[i].setForeground(Color.GREEN);
                    coins[i].setText(next.get(i).toString() + " $");
                    if (i == crypto.size() - 1)
                        Thread.sleep(10000);
                } catch (Exception a) {
                    System.out.println("1 = " + a);
                }
            }
        }
    }

    public void getTrading() {
        for (;;) {
            if (!TradingFrame.isVisible())
                break;
            Image scaledImage = img.get(symbol.getSelectedIndex()).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); //Mettre ca dans une liste
            coin.setIcon(new ImageIcon(scaledImage));
            coin.setText(coins[symbol.getSelectedIndex()].getText());
        }
    }

    public List<JComponent> getComp() {
        List<JComponent> list = new ArrayList<JComponent>();
        list.add(symbol);
        list.add(coin);
        list.add(leverage);
        list.add(valueLeverage);
        list.add(amount);
        list.add(Longit);
        list.add(Shortit);
        return list;
    }

    public void setMenu() {
        Menu.setLayout(new FlowLayout());
        Menu.setBackground(gay);
        Bmarket.setBackground(Color.WHITE);
        Btrading.setBackground(Color.WHITE);
        Bmarket.setForeground(Color.DARK_GRAY);
        Btrading.setForeground(Color.DARK_GRAY);
        Bmarket.setFocusPainted(false);
        Btrading.setFocusPainted(false);
        Menu.add(Bmarket);
        Menu.add(Btrading);
        Bmarket.addActionListener(this);
        Btrading.addActionListener(this);
    }

    public void setFrame() {
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setPreferredSize(new Dimension(1080, 720));
        win.setVisible(true);
        win.setLayout(new BorderLayout());
        win.setBackground(Color.BLACK);
        win.add(Menu, BorderLayout.NORTH);
    }

    public void setMarketFrame() {
        /* set Panels for MarketFrame */
        MarketFrame.setLayout(new GridBagLayout());
        MarketFrame.setBackground(Color.BLACK);
        
            /* Icon and Prices of Cryptocurrency */
        for (int i = 0; i < crypto.size(); i++) {
            coins[i] = new JLabel();
            coins[i].setIcon(img.get(i));
            coins[i].setForeground(Color.GREEN);
            coins[i].setIconTextGap(20);
            coins[i].setFont(new Font("Arial", Font.BOLD, 20));
        }
        
            /* Adding Components to Panels */
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(50, 50, 100, 50);
        for (int i = 0; i < crypto.size(); i++) {
            c.gridx = i % 3;
            c.gridy = i / 3;
            MarketFrame.add(coins[i], c);
        }
            /* Adding panel to frame */
        win.add(MarketFrame, BorderLayout.CENTER);
        MarketFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Bmarket) {
            if (!MarketFrame.isVisible()) {
                win.remove(TradingFrame);
                TradingFrame.setVisible(false);
                win.add(MarketFrame, BorderLayout.CENTER);
                MarketFrame.repaint();
                MarketFrame.setVisible(true);
                System.out.println("Market");
            }
        }
        if (e.getSource() == Btrading) {
            if (!TradingFrame.isVisible()) {
                win.remove(MarketFrame);
                MarketFrame.setVisible(false);
                win.add(TradingFrame, BorderLayout.CENTER);
                TradingFrame.repaint();
                TradingFrame.setVisible(true);
                Image scaledImage = img.get(symbol.getSelectedIndex()).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                coin.setIcon(new ImageIcon(scaledImage));
                coin.setText(coins[symbol.getSelectedIndex()].getText());
                System.out.println("Trading");
            }
        }
        if (e.getSource() == Longit) {
            System.out.println(symbol.getSelectedItem() + " " + valueLeverage.getText().replace("x", "") + " " + amount.getText() + " " + "Long");
        }
        if (e.getSource() == Shortit) {
            System.out.println(symbol.getSelectedItem() + " " + valueLeverage.getText().replace("x", "") + " " + amount.getText() + " " + "Short");
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        valueLeverage.setText("x" + source.getValue());
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (amount.getText().equals("Amount in $"))
            amount.setText(null);
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (amount.getText().isEmpty())
            amount.setText("Amount in $");
    }
}