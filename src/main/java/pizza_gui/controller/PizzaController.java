package pizza_gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import pizza_gui.model.Ingredient;
import pizza_gui.model.Pizza;
import pizza_gui.model.PizzaModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PizzaController {
    // Aby dodać kolekcję obiektów do kontrolek FXML korzystamy ObservableList
    private ObservableList<PizzaModel> pizzas = FXCollections.observableArrayList();
    // -WPROWADŹ PIZZE z enum Pizza do listy pizzas ---------------
    private void addPizzas(){
        // type -> WEGE lub SPICY
        for (Pizza pizza : Pizza.values()){
            pizzas.add(new PizzaModel(
                    pizza.getName(),
                    pizza.getIngredients().stream().map(Ingredient::getName).collect(Collectors.joining(",")),
                    (pizza.getIngredients().stream().anyMatch(Ingredient::isSpicy) ? "ostra " : " ")
                            +
                         (pizza.getIngredients().stream().noneMatch(Ingredient::isMeat) ? "wege " : " "),
                    pizza.getIngredients().stream().mapToDouble(Ingredient::getPrice).sum()
            ));
        }
    }
    // ------------------------------------------------------------
    @FXML
    private TableView<PizzaModel> tblPizza;         // Klasa modelu
    @FXML
    private TableColumn<PizzaModel, String> tcName; // Klasa modelu, typ danych
    @FXML
    private TableColumn<PizzaModel, String> tcIngredients;
    @FXML
    private TableColumn<PizzaModel, String> tcType;
    @FXML
    private TableColumn<PizzaModel, Double> tcPrice;
    @FXML
    private Label lblRandomPizza;
    @FXML
    private TextArea taBasket;
    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfPhone;

    @FXML
    void clearAction(MouseEvent event) {
        System.out.println("CLEAR");
    }

    @FXML
    void ordrerAction(MouseEvent event) {
        System.out.println("ORDER");
    }

}
