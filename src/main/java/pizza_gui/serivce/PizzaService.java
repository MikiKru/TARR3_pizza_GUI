package pizza_gui.serivce;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import pizza_gui.model.Ingredient;
import pizza_gui.model.Pizza;
import pizza_gui.model.PizzaModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.regex.Pattern;
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
        randomPizza.setText(String.format("%s - %.2f zł", pizzaOfTheDay.getName(),pizzaOfTheDay.getPrice()));
    }
    private List<Integer> choices = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
    // obiekt przechowujący kwotę do zapłaty
    private double amount;
    // metoda do wybierania i przenoszenia pizzy do koszyka
    public void addToBasket(TableView<PizzaModel> tblPizza, TextArea taBasket, Label lblSum){
        // odczyt, który wiersz w tabelce został zaznaczony
        PizzaModel selectedPizza = tblPizza.getSelectionModel().getSelectedItem();
        // utworzenie okna kontekstowego do zamówienia wybranej ilości pizzy
        ChoiceDialog<Integer> addToBasketDialog = new ChoiceDialog<>(1, choices);
        addToBasketDialog.setTitle("Wybierz ilość");
        addToBasketDialog.setHeaderText("Wybrałeś pizze " + selectedPizza.getName());
        addToBasketDialog.setContentText("Wybierz ilość zamawianej pizzy: ");
        // okno zostaje wyświetlone i utrzymane na ekranie i zwróci wartość po wciśnięciu przycisku
        Optional<Integer> result = addToBasketDialog.showAndWait();
        // gdy wybrano OK
        result.ifPresent(quantity -> taBasket.appendText(
                String.format("%-15s %5d szt. %10.2f zł\n",
                        selectedPizza.getName(),quantity, selectedPizza.getPrice() * quantity)));
        // gdy wybrano OK
        result.ifPresent(quantity -> amount = amount + (quantity * selectedPizza.getPrice()));
        lblSum.setText(String.format("KWOTA DO ZAPŁATY: %.2f ZŁ", amount));
    }
    public void clearOrder(TextArea taBasket, TextField tfAddress, TextField tfPhone, Label lblSum){
        taBasket.clear();
        tfAddress.clear();
        tfPhone.clear();
        amount = 0;     // czyszczenie wartości globalnej
        lblSum.setText("KWOTA DO ZAPŁATY: 0.00 ZŁ");
    }
    public boolean isPhoneValid(String phone){
        return Pattern.matches("(^([0-9]{3}[-]{1}){2}[0-9]{3}$)|(^[0-9]{9}$)", phone);
    }
    public boolean isAddressValid(String address){
        return Pattern.matches("^[au][l][\\.]\\s{0,1}[A-Za-złąęśćźżóń\\d\\.\\s]{1,}\\s{1}\\d{1,}[A-Za-z]{0,}[\\/]{0,1}\\d{0,}[,]\\s{0,1}\\d{2}[-]\\d{3}\\s{1}[A-Za-złąęśćźżóń\\s\\-]{2,}$",
                address);
    }
    // okno dialogowe typu information lub error potwierdzające lub odrzucające zamówienie
    public void getOrder(TextField tfPhone, TextField tfAddress, TextArea taBasket, Label lblSum){
        if(isPhoneValid(tfPhone.getText()) && isAddressValid(tfAddress.getText()) && !taBasket.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Zamówienie");
            alert.setHeaderText("Potwierdzenie zamówienia");
            alert.setContentText("Twoje zamówienie: \n" + taBasket.getText() +"\nDo zapłaty: " + amount + " zł");
            alert.showAndWait();
            saveDataToFile(tfAddress,tfPhone,taBasket);
            clearOrder(taBasket,tfAddress, tfPhone,lblSum); // czyści pola - koszyk, adres, telefon i sumę do zapłaty
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Zamówienie");
            alert.setHeaderText("Błędne dane zamówienia");
            String validationResult = "Wprowadziłeś niepoprawne dane w następujących polach: ";
            if(!isPhoneValid(tfPhone.getText())){
                validationResult += "telefon ";
            }
            if(!isAddressValid(tfAddress.getText())){
                validationResult += "adres dostawy ";
            }
            if(isPhoneValid(tfPhone.getText()) && isAddressValid(tfAddress.getText())){
                validationResult = "";
            }
            String emptyBasket = "";
            if(taBasket.getText().equals("")){
                emptyBasket = "\nTwój koszyk z zamówieniami nie może być pusty.";
            }
            alert.setContentText(validationResult + emptyBasket);
            alert.showAndWait();
        }
    }
    public void saveDataToFile(TextField tfAddress, TextField tfPhone, TextArea taBasket) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");    // konfiguracja formatu wyświetlania daty
        try {
        FileChooser fileChooser = new FileChooser();
        // konfiguracja filtra rozszerzeń plików
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Plik tekstowy (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(null);
        PrintWriter printWriter = null;
        printWriter = new PrintWriter(file);
        printWriter.println("POTWIERDZENIE ZAMÓWIENIA");
        LocalDateTime dateTime = LocalDateTime.now();
        printWriter.println("Data i czas: " + dateTime.format(dtf)); // data do sformatowania dd.mm.YYYY hh:mm
        printWriter.println("Adres dostawy: " + tfAddress.getText());
        printWriter.println("Telefon kontaktowy: " + tfPhone.getText());
        printWriter.println("Czas dostawy: " + dateTime.plusMinutes(45).format(dtf));
        printWriter.println("Produkty: \n" + taBasket.getText());
        printWriter.println(String.format("Suma do zapłaty : %.2f zł", amount));
        printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void clock(Label lblClock){
        // --------------- IMPLEMENTCJA ------------------------
        Thread clockThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                while (true){                                        // pętla nieskończona
                    try {
                        String result = LocalDateTime.now().format(dateTimeFormatter);
                        Platform.runLater(() -> {
                            lblClock.setText(result);
                        });
                        Thread.currentThread().sleep(1000);     // uśpienie wątku clock thread na 1s
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // ------------------------------------------------------
        clockThread.start();        // uruchomienie wątku clock thread -> wywołanie metody run() wykonywanej przez inny procesor
    }
    public void timeToExit(ProgressBar pgExit){
        Thread pgThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++){
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    double progress = i / 100.0;
                    pgExit.setProgress(progress);
                }
                Platform.exit();        // automatyczn
            }
        });
        pgThread.start();
    }
}
