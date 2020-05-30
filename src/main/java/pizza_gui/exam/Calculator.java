package pizza_gui.exam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calculator {
    int num = 100;
    public void calc(int num) {
        this.num = num * 10;
    }
    public void printNum(){
        System.out.println(this.num);
    }
    public static void main(String[] args) {
        Calculator obj = new Calculator ();
        obj.calc(2);
        obj.printNum();

        List p = new ArrayList();
        p.add(7);
        p.add(1);
        p.add(5);
        p.add(1);
        Integer number = 1;
//        p.remove(number);
        p.removeAll(new ArrayList<>(Arrays.asList(number)));
        System.out.println(p);
    }
}
