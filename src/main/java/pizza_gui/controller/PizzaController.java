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
import pizza_gui.serivce.PizzaService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PizzaController {

    // Aby dodać kolekcję obiektów do kontrolek FXML korzystamy ObservableList
    private ObservableList<PizzaModel> pizzas = FXCollections.observableArrayList();
    // Obiekt klasy Pizza service
    private PizzaService pizzaService = new PizzaService();

    @FXML
    private Label lblSum;
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
        pizzaService.clearOrder(taBasket, tfAddress, tfPhone, lblSum);
    }
    @FXML
    void orderAction(MouseEvent event) {
        pizzaService.getOrder(tfPhone,tfAddress,taBasket,lblSum);
    }
    @FXML
    void selectPizzaAction(MouseEvent mouseEvent) {
        pizzaService.addToBasket(tblPizza, taBasket, lblSum);
    }
    // konstruktor -> inicjalizacja GUI
    public void initialize(){
        // wywołanie metod zaimplementowanych w logice biznesowej aplikacji
        pizzas = pizzaService.addPizzas(pizzas);
        pizzaService.insertPizzasToTable(tblPizza, tcName, tcIngredients, tcType, tcPrice, pizzas);
        pizzaService.pizzaOfTheDayGenerator(pizzas, lblRandomPizza);
    }


}
