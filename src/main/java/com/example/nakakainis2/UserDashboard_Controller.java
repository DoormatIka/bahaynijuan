package com.example.nakakainis2;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class UserDashboard_Controller {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnDonate;

    @FXML
    private Button btnInventory;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnMap;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblDashboard;

    @FXML
    private AnchorPane lblExpiringSoon;

    @FXML
    private AnchorPane lblRecentDonation;

    @FXML
    private AnchorPane lblTotalGoods;

    // Refactor this controller to handle all user dashboard sections (dashboard,
    // inventory, donate, map)

}