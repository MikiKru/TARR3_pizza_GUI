package pizza_gui.exam;

import java.util.List;

public interface ExamInt {
//    public void method1() {
//        System.out.println("method1");
//    }
    public default Long method2(List<String> names) {
        return names.stream().count();
    }
    public static int number = 0;
    public static void method3(){
        System.out.println("Metoda statyczna: " + number);
    }
    public abstract void method4();

}
