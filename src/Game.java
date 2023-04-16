import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        ScoresUpdate();
    }

    public void Scores(Graphics g){
        g.setColor(Color.WHITE);
        String l = Integer.toString(LeftScore); //this is causing a delay
        String r = Integer.toString(RightScore);
        g.drawString(l, width / 2 - 50, 50);
        g.drawString(r, width / 2 + 50, 50);
    }
    public void ScoresUpdate(){
        if(ball.position.x > width){
            LeftScore++;
            resetBall("right");
        }
        if(ball.position.x < 0){
            RightScore++;
            resetBall("left");
        }
    }
    public void resetBall(String direction){
        ball.position.x = width/2;
        ball.position.y = height/2;
        if(direction.equals("left")){
            ball.velocity.x = -250;
        }
        if(direction.equals("right")){
            ball.velocity.x = 250;
        }
        ball.velocity.y = 0;
    }
}

public class Game extends JPanel implements KeyListener {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 400;
    public static final int FPS = 60;
    World world;

    Boolean placeCoffee;
    class Runner implements Runnable{
        public void run() {
            while (true) {
                world.updateShapes(1.0 / (double) FPS);
                repaint();
                try {
                    Thread.sleep(1000 / FPS);
                } catch (InterruptedException e) {
                }
            }
        }
    }
    public Game(){
        placeCoffee = false;
        world = new World(WIDTH, HEIGHT);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Game.Runner());
        mainThread.start();
    }



    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        System.out.println("You pressed down: " + c);
        int velocity = 400;
        if(c =='c'){ //coffee will be shown
            placeCoffee = true;
        }
        if(c=='s'){ //stops coffee from being shown
            placeCoffee = false;
        }
    }

    public static void main (String[] args){
        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game mainInstance = new Game();
        frame.setContentPane(mainInstance);
        frame.pack();
        frame.setVisible(true);

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Toolkit t=Toolkit.getDefaultToolkit(); //this allows us to get the image
        Image i=t.getImage("src/KitchenCounter.png"); //this is the file path
        g.drawImage(i, 0,0,null); //this draws image i

        if(placeCoffee){
            Image coffee = t.getImage("src/FilledLatte.png"); //this is the file path
            g.drawImage(coffee, WIDTH/2- coffee.getWidth(null)/2,300,null); //this draws coffee
        }

       // world.drawShapes(g);
    }

    public void keyReleased(KeyEvent e) {
        char c=e.getKeyChar();
    }
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
    }
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
}
