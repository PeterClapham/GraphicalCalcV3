import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Graph extends JFrame {

    // For this iteration, the size of the graph space will not be scalable.
    // The domain will be static at (-100, 100) and the range will not be defined.
    // This iteration's intention is to be a clean implementation, to be developed in the next.

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("Input the equation");
        String s = sc.next();
        sc.close();
        new Graph(s);
    }

    private final int SIZE = 750;
    private final int domainMin = -10;
    private final int domainMax = 10;
    private int currentX;
    private int currentY; // Don't want this variable, try to make local
    private int prevX;
    private int prevY;
    private String postfix;
    // MAKE SHUNTING YARD A LIBRARY!!
    private ShuntingYard s;
    private GeneralPath path;
    private boolean outBounds;

    private Graph(String title){
        // Setup JFrame
        setTitle(title);
        setSize(SIZE, SIZE);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Initialize co-ordinate state i.e. the PIXEL POSITION
        currentX = 29;
        currentY = 0;
        // Create Shunting Yard object
        s = new ShuntingYard();
        // Create postfix equation
        postfix = s.postfix(title);
        System.out.println(postfix);
        // Initialize path
        path = new GeneralPath();
        run();
        path.moveTo(currentX, currentY);
        outBounds = true;
    }

    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        // Draw axes
        if (currentX != 30) {
            g.drawLine(SIZE / 2, 30, SIZE / 2, SIZE - 30);
            g.drawLine(30, SIZE / 2, SIZE - 30, SIZE / 2);
        }
        g2.setColor(Color.BLUE);
        // System.out.println("x " + currentX + " y " + currentY); FOR TESTING.
        if (inBounds() && !outBounds){
            path.quadTo(prevX, prevY, currentX, currentY);
            outBounds = false;
        } else if(inBounds()){
            path.moveTo(currentX, currentY);
        }
        outBounds = !inBounds();
        if (currentX < SIZE - 30){
            repaint();
            run();
        } else {
            g2.draw(path);
        }
    }

    private boolean inBounds(){
        return (currentX < SIZE - 30 && currentX > 30 && currentY > 30 && currentY < SIZE - 30);
    }

    /**
     * Calculates the co-ordinates of the graph at position x, y.
     */
    private void run(){
        double eqnX = scaleBetween(currentX, domainMin, domainMax, 30, SIZE - 30);
        double eqnY = subNumbers(eqnX);
        // System.out.println("eqnX " + eqnX + " eqnY " + eqnY); FOR TESTING.
        // If range is introduced, restrict y
        prevY = currentY;
        prevX = currentX;
        currentY = (int) scaleBetween(eqnY, SIZE - 30, 30, domainMin, domainMax);
        currentX ++;
    }

    private double scaleBetween(double unscaledNum, double minAllowed, double maxAllowed, double min, double max) {
        return (maxAllowed - minAllowed) * (unscaledNum - min) / (max - min) + minAllowed;
    }

    /**
     * Substitutes the number 'x' into the reverse Polish equation
     * and outputs the 'y' co-ordinate
     * @param x The number to be substituted into the equation
     * @return The 'y' co-ordinate
     */
    private double subNumbers(double x){
        Stack<String> eqn = new Stack<>();
        String token;
        double num1;
        double num2;
        int[] aCount = {-1};
        ArrayList<Double> aList = new ArrayList<>();
        for (int i = 0; i < postfix.length(); i++) {
            token = postfix.substring(i, i+1);
            if (s.fnc.containsKey(token)) {
                num1 = checkNum(eqn, x, aCount, aList);
                switch (token) { // FIGURE OUT THE ADD COMMA -> TO SEPARATE NUMBERS I.E. 23, 5 IN A STRING
                    case "s":
                        aList.add(Math.sin(num1));
                        break;
                    case "c":
                        aList.add(Math.cos(num1));
                        break;
                    case "t":
                        aList.add(Math.tan(num1));
                        break;
                    case "v":
                        aList.add(1/Math.sin(num1));
                        break;
                    case "$":
                        aList.add(1/Math.cos(num1));
                        break;
                    case "u":
                        aList.add(1/Math.tan(num1));
                        break;
                    case "z":
                        aList.add((Math.exp(num1) - Math.exp(-num1))/2);
                        break;
                    case "h":
                        aList.add((Math.exp(num1) + Math.exp(-num1))/2);
                        break;
                    case "r":
                        aList.add(
                                ((Math.exp(num1) - Math.exp(-num1))/2)
                                        /
                                        ((Math.exp(num1) + Math.exp(-num1))/2));
                        break;
                    case "l":
                        aList.add(Math.log10(num1));
                        break;
                    case "n":
                        aList.add(Math.log(num1));
                        break;
                    case "q":
                        aList.add(Math.acos(num1));
                        break;
                    case "w":
                        aList.add(Math.asin(num1));
                        break;
                    case"f":
                        aList.add(Math.atan(num1));
                }
                eqn.push("a");
                aCount[0] ++;
            } else if(s.ops.containsKey(token)) {
                num1 = checkNum(eqn, x, aCount, aList);
                num2 = checkNum(eqn, x, aCount, aList);
                switch (token) {
                    case "+":
                        aList.add(num2 + num1);
                        break;
                    case "-":
                        aList.add(num2 - num1);
                        break;
                    case "*":
                        aList.add(num2 * num1);
                        break;
                    case "/":
                        aList.add(num2 / num1);
                        break;
                    case "^":
                        aList.add(Math.pow(num2, num1));
                }
                eqn.push("a");
                aCount[0] ++;
            } else {
                eqn.push(token);
            }
        }
        return aList.get(0);
    }

    /**
     * Evaluates the number and outputs what it should be equal to
     * @param eqn The postfix in the form of a stack
     * @param x The value to be evaluated
     * @param aCount The amount of calculated constants
     * @param aList The list of constants
     * @return The new number
     */
    private double checkNum(Stack<String> eqn, double x, int[] aCount, ArrayList<Double> aList){
        String s = eqn.pop();
        switch (s) {
            case "x":
                return x;
            case "e":
                return Math.exp(1);
            case "a":
                double num = aList.get(aCount[0]);
                aList.remove(aCount[0]);
                aCount[0] --;
                return num;
            default:
                return Double.parseDouble(s);
        }
    }

}
