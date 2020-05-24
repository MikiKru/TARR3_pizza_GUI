package pizza_gui.serivce;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pizza_gui.model.Ingredient;
import pizza_gui.model.Pizza;
import pizza_gui.model.PizzaModel;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class PizzaService {
    // metoda wprowadzająca dane do ObservableList
    public ObservableList<PizzaModel> addPizzas(ObservableList<PizzaModel> pizzas){
        for (Pizza pizza : Pizza.values()){
            pizzas.add(new PizzaModel(
                    pizza.getName(),
                    pizza.getIngredients().stream().map(Ingredient::getName).collect(Collectors.joining(",")),
                    (pizza.getIngredients().stream().anyMatch(Ingredient::isSpicy) ? "ostra " : "")
                            +
                            (pizza.getIngredients().stream().noneMatch(Ingredient::isMeat) ? "wege " : ""),
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
      // ustawienie języka i formatowanie wartości double
      Locale locale = new Locale("pl", "PL");
      // obiekt do wartości numerycznych
      NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
      tcPrice.setCellFactory(tc -> new TableCell<PizzaModel,Double>(){
            @Override
            protected void updateItem(Double price, boolean empty){
                super.updateItem(price, empty);
                if(empty){
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
      });
      tblPizza.setItems(pizzas);
    }
    // generator pizzy dnia -> 1. obniżenie ceny wylosowanej pizzy 2. przekazanie nazwy pizzy dania do Label-a
    public void pizzaOfTheDayGenerator(ObservableList<PizzaModel> pizzas, Label randomPizza){
        // losowanie Pizzy
        int randomIndex = new Random().nextInt(pizzas.size());
        PizzaModel pizzaOfTheDay = pizzas.get(randomIndex);
        // obniżenie ceny pizzy dania o 20%
        pizzas.get(randomIndex).setPrice(pizzas.get(randomIndex).getPrice() * 0.8);
        // wypisanie nazwy pizzy w Labelu
        randomPizza.setText(pizzaOfTheDay.getName() + " - " + pizzaOfTheDay.getPrice() + " zł");
    }

}
