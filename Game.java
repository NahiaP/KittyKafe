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

class Pair{
    //create positionX and positionY, and same for acceleration and velocity
    public double x;
    double y;

    public Pair(double x, double y){ //create a pair of coordinates
        this.x = x;
        this.y = y;
    }
    public void flipX(){
        this.x = -1*x;
    } //make shape go opposite x direction
    public void flipY(){
        this.y = -1*y;
    } //make shape go opposite y direction
    public Pair add(Pair velocity){  //want to add x velocity to x position and y velocity to y position
        Pair newpair = new Pair(x + velocity.x,y + velocity.y);
        return newpair;
    }
    public Pair times(double time){ //needs to return velocity multiplied by time
        Pair newpair = new Pair(x*time,y*time);
        return newpair;
    }
    public Pair divide(double dampening){ //divide velocity by damping if bounced
        Pair newpair = new Pair(x/dampening,y/dampening);
        return newpair;
    }
}

class Ball{
    Pair position;
    Pair velocity;
    double width;
    double halfwidth;
    Color color;
    World w;
    public Ball(World w) {
        this.w = w;
        position = new Pair(w.width/2, w.height/2);
        //make random
        velocity = new Pair(400, 0);
        width = 50;
        halfwidth = width / 2.0;
        color = Color.WHITE;
    }

    public void update(World w, double time){
        position = position.add(velocity.times(time)); //position = position + velocity*time
        bounce(w); //change velocity if shape hits window sides
    }
    public void draw(Graphics g){
        Color c = g.getColor();
        g.setColor(color);
        g.fillOval((int)(position.x - halfwidth), (int)(position.y - halfwidth), (int)(width), (int)(width));
    }

    private void bounce(World w){
        if (position.y <= 0){
            position.y = 10;
            velocity.flipY();
        }
        if(position.y >= w.height){
            position.y = 590;
            velocity.flipY();
        }
        //center of ball must hit the paddle in order for the ball to bounce
        if(position.y == w.pLeft.position.y && position.x > 20 && position.x <= 30){
            straightBounce(31,250,0);
        }
        if(position.y == w.pRight.position.y && position.x > 570 && position.x < 580){
            straightBounce( 570,-250,0);
        }
        if(position.x > 20 && position.x <= 30 && position.y +width/2 >= (w.pLeft.position.y-w.pLeft.halfheight) && position.y -width/2 <= (w.pLeft.position.y+w.pLeft.halfheight)){ //if ball within paddle range
            createAngle(w.pLeft);
        }
        if(position.x > 570 && position.x < 580 && position.y +width/2 >= (w.pRight.position.y-w.pRight.halfheight) && position.y-width/2 <= (w.pRight.position.y+w.pRight.halfheight)){ //if ball within paddle range
            createAngle(w.pRight);
        }
    }
    private void straightBounce(int pos, int velX, int velY){
        position.x = pos;
        velocity.x = velX;
        velocity.y = velY;
        System.out.println("Angle in degrees: " + Math.atan(velocity.y/velocity.x)*180/Math.PI);
    }
    private double calculateDist(Paddle p){
        double dist = position.y-(p.position.y); //maximum distance is 85, minimum is -85
        return dist;
    }
    private void createAngle(Paddle p){
        velocity.flipX(); //velocity x is always 250
        double magnitudeHelper = (calculateDist(p)/(85)*3.606); //maximum value is 3.606, minimum is -3.606
        velocity.y = 250 * magnitudeHelper; //if distance is negative, velocity is negative and ball goes up; reverse is true if distance is positive
                                            //maximum velocityY is 901.5, minimum is - 901.5
                                            //velocityX is always 250, so the arcTan will make it so that the angle is a maximum of 75 and a minimum of -75
        System.out.println("Angle in degrees: " + Math.atan(velocity.y/velocity.x)*180/Math.PI); //convert arctan to degrees
    }
}

class Paddle {
    Pair position;
    Pair velocity;
    double width;
    double halfwidth;
    double height;
    double halfheight;
    Color color;
    World w;

    public Paddle(int pX, World w) {
        this.w = w;
        position = new Pair(pX, 300);
        velocity = new Pair(0, 0);
        width = 20;
        halfwidth = width / 2.0;
        height = 120;
        halfheight = height / 2.0;
        color = Color.WHITE;
    }

    public void update(World w, double time) {
        position = position.add(velocity.times(time)); //position = position + velocity*time
        if(position.y >= w.height-halfheight){
            velocity.y = 0;
        }
        if(position.y <= halfheight){
            velocity.y = 0;
        }
    }

    public void draw(Graphics g) {

        g.setColor(Color.WHITE);
        g.fillRect((int)position.x, (int) position.y - (int) halfheight, (int)width, (int)height);
    }
}

class World{
    int height;
    int width;
    Ball ball;
    Paddle pLeft;
    Paddle pRight;
    int LeftScore;
    int RightScore;

    public World(int initWidth, int initHeight){
        width = initWidth;
        height = initHeight;
        ball = new Ball(this);
        pLeft = new Paddle(0,this);
        pRight = new Paddle(width-20,this);
        LeftScore = 0;
        RightScore = 0;
    }

    public void drawShapes(Graphics g){ //draw shape in shapes array
        pLeft.draw(g);
        pRight.draw(g);
        ball.draw(g);
    }
    public void updateShapes(double time){
        pLeft.update(this,time);
        pRight.update(this,time);
        ball.update(this,time);
    }
}

public class Game extends JPanel implements KeyListener, MouseListener {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 400;
    public static final int FPS = 60;
    World world;
    Boolean placeCoffee, placePotion;
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
    public Game(){
        addMouseListener(this);
        addedIngredients=new ArrayList<String>();
        background = "StartScreen";
        placeCoffee = false;
        placePotion = false;
        world = new World(WIDTH, HEIGHT);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Game.Runner());
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
                    placeCoffee = true;
                    addedIngredients.add("coffee");
                }
                if(x < 160 && x > 100 && y<140 && y > 80){
                    placePotion = true;
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
        }
    }

    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        System.out.println("You pressed down: " + c);
        switch(c){
            case 's':
                placeCoffee = false; //stops coffee from being shown
                placePotion = false; //stops potion from being shown
                break;
            case 'e':
                background ="StartScreen";
                break;
        }
    }

    public static void main (String[] args){
        JFrame frame = new JFrame("GameTesting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game mainInstance = new Game();
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
                    if(s == "coffee"){
                        Image coffee = t.getImage("src/FilledLatte.png"); //this is the file path
                        showImage(g, "src/FilledLatte.png", WIDTH / 2 - coffee.getWidth(null) / 2,300);
                    }
                    if(s == "potion"){
                        Image coffee = t.getImage("src/Potion.png"); //this is the file path
                        showImage(g, "src/Potion.png", WIDTH / 2 - coffee.getWidth(null) / 2,300);
                    }
                }
                /*
                if (placeCoffee) {
                    Image coffee = t.getImage("src/FilledLatte.png"); //this is the file path
                    showImage(g, "src/FilledLatte.png", WIDTH / 2 - coffee.getWidth(null) / 2,300);
                }
                if (placePotion) {
                    Image potion = t.getImage("src/Potion.png"); //this is the file path
                    showImage(g, "src/Potion.png", WIDTH / 2 - potion.getWidth(null) / 2,300);
                }

                 */
                break;

        }
       // world.drawShapes(g);
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
