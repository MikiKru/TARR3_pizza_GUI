package pizza_gui.serivce;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pizza_gui.model.Ingredient;
import pizza_gui.model.Pizza;
import pizza_gui.model.PizzaModel;

import java.util.stream.Collectors;

public class PizzaService {
    // metoda wprowadzająca dane do ObservableList
    public ObservableList<PizzaModel> addPizzas(ObservableList<PizzaModel> pizzas){
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
        return pizzas;
    }
    // metoda konfigurująca kolumny TableView i wprowadzająca dane z ObservableList
    public void insertPizzasToTable(
            TableView<PizzaModel> tblPizza,
            TableColumn<PizzaModel, String> tcName,
            TableColumn<PizzaModel, String> tcIngredients,
            TableColumn<PizzaModel, String> tcType,
            TableColumn<PizzaModel, Double> tcPrice,
            ObservableList<PizzaModel> pizzas
    ){
      tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
      tcIngredients.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
      tcType.setCellValueFactory(new PropertyValueFactory<>("type"));
      tcPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
      tblPizza.setItems(pizzas);
    }
}
