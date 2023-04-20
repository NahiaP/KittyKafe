import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
/*
resources used:
https://www.geeksforgeeks.org/java-signum-method-examples/
https://www.cuemath.com/geometry/radians-to-degrees/
https://www.educba.com/repaint-in-java/
 */
class Cat{
    String desiredOrder;
    int impatienceFactor; //This could be the variable that measures how quickly the customer wants their order,
    //the score that the player gets could be deterimend by comparing this value with the time it took to complete the order
    Boolean isOrderFilled = false;
    long startTime;
    long endTime;
    int xPosition;
    int yPosition;
    public Cat(String order, int impatienceFactor) {
        desiredOrder = order;
        startTime = System.nanoTime(); //tracks time when customer was created. Once order is fulfilled can compare start time
        //with time that order was fulfilled at

    }
    public void update(GameWorld w) {

        if (isOrderFilled) {
            //calculate score
            long timeElapsed = endTime - startTime;


            w.currentCustomers.remove(this);//remove from list of current customers
            //add new customer?
        }
    }





}

class GameWorld{
    int height;
    int width;
    int score = 0; //tracks player score
    ArrayList<Cat> currentCustomers; //list of current customers

    public GameWorld(int initWidth, int initHeight){
        width = initWidth;
        height = initHeight;
        currentCustomers = new ArrayList<Cat>();
        Cat testCat = new Cat("coffee", 1);
        currentCustomers.add(testCat);
    }

    public void drawShapes(Graphics g){ //draw shape in shapes array
    }
    public void updateCustomers(){

    }
}

public class GameTesting extends JPanel implements KeyListener, MouseListener {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 400;
    public static final int FPS = 60;
    GameWorld world;
    String background;
    ArrayList<String> addedIngredients;
    class Runner implements Runnable{
        public void run() {
            while (true) {
                repaint();
                try {
                    Thread.sleep(1000 / FPS);
                } catch (InterruptedException e) {
                }
            }
        }
    }
    public GameTesting(){
        addMouseListener(this);
        addedIngredients=new ArrayList<String>();
        background = "StartScreen";
        world = new GameWorld(WIDTH, HEIGHT);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new GameTesting.Runner());
        mainThread.start();
    }

    public void mousePressed(MouseEvent e){
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation(); //returns mouse location as a point object
        int x = (int) b.getX(); //gets x position
        int y = (int) b.getY(); //gets y position
        System.out.println("x: " + x + " y: "+ y);
        switch(background){
            case "StartScreen":
                if(x < 320 && x > 70 && y<370 && y > 300){ //if you click the button
                    background = "KitchenCounter";
                    break;
                }
            case "KitchenCounter":
                if(x < 300 && x > 85 && y<235 && y > 175){
                    addedIngredients.add("coffee");
                }
                if(x < 160 && x > 100 && y<140 && y > 80){
                    addedIngredients.add("potion");
                }
                if(x < 880 && x > 690 && y<130 && y > 65){ //if you click the button
                    background = "Register";
                    break;
                }
                break;
            case "Register":
                if(x < 880 && x > 690 && y<130 && y > 65){ //if you click the button
                    background = "KitchenCounter";
                    break;
                }
                for (Cat customer : world.currentCustomers) { //basic form of code, need to figure how to check exactly if a customer was clicked
                    if (x < customer.xPosition && x > customer.xPosition - 10 && y < customer.yPosition && y > customer.yPosition - 10) {
                        String preparedOrder = "";
                        for (String ingredient : addedIngredients) { //takes all of the added ingredients and converts to one string
                            preparedOrder += ingredient + " ";
                        }
                        if (customer.desiredOrder == preparedOrder) { // if desired order is equal to prepared order
                            for (String ingredient : addedIngredients) {
                                addedIngredients.remove(ingredient); // removes ingredients from added ingredients
                            }
                            customer.endTime = System.nanoTime(); // will be used to calcuate elapsed time
                            customer.isOrderFilled = true;
                        }
                        break;
                    }
                }
        }
    }

    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        System.out.println("You pressed down: " + c);
        switch(c){
            case 's':
                addedIngredients.clear();
                break;
            case 'e':
                background ="StartScreen";
                break;
        }
    }

    public static void main (String[] args){
        JFrame frame = new JFrame("GameTesting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameTesting mainInstance = new GameTesting();
        frame.setContentPane(mainInstance);
        frame.pack();
        frame.setVisible(true);

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Toolkit t=Toolkit.getDefaultToolkit(); //this allows us to get the image

        switch(background){
            case "StartScreen":
                showImage(g, "src/StartScreen.png", 0,0);
                break;
            case "Register":
                showImage(g, "src/Register.png", 0,0);
                break;
            case "KitchenCounter":
                showImage(g, "src/KitchenCounter.png", 0,0);

                for(String s : addedIngredients){
                    if(s.equals("coffee")){
                        Image coffee = t.getImage("src/FilledLatte.png"); //this is the file path
                        showImage(g, "src/FilledLatte.png", WIDTH / 2 - coffee.getWidth(null) / 2,300);
                    }
                    if(s.equals("potion")){
                        Image coffee = t.getImage("src/Potion.png"); //this is the file path
                        showImage(g, "src/Potion.png", WIDTH / 2 - coffee.getWidth(null) / 2,300);
                    }
                }
                break;

        }
    }

    public static void showImage(Graphics g, String filePath, int xPos, int yPos){
        Toolkit t=Toolkit.getDefaultToolkit();
        Image i= t.getImage(filePath); //this is the file path
        g.drawImage(i, xPos,yPos,null);
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void mouseClicked(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
}