import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.Random;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
class Pair{
    public double x;
    public double y;
     
    public Pair(double initX, double initY){
    	x = initX;
    	y = initY;
    }
 
    public Pair add(Pair toAdd){
    	return new Pair(x + toAdd.x, y + toAdd.y);
    }
 
    public Pair divide(double denom){
    	return new Pair(x / denom, y / denom);
    }
 
    public Pair times(double val){
    	return new Pair(x * val, y * val);
    }
 
    public void flipX(){
    	x = -x;
    }
     
    public void flipY(){
    	y = -y;
    }
}
 
class Sphere{
    Pair position;
    Pair velocity;
    Pair acceleration;
    double radius;
    double dampening;
    Color color;
    public Sphere() {
    Random rand = new Random(); 
    position = new Pair(500.0, 500.0);
    velocity = new Pair((double)(rand.nextInt(1000) - 500), (double)(rand.nextInt(1000) - 500));
    acceleration = new Pair(0.0, 200.0);
    radius = 25;
    dampening = 1.3;
    color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }
    
    public void update(World w, double time){
    position = position.add(velocity.times(time));
    velocity = velocity.add(acceleration.times(time));
    bounce(w);
    }
    public void ClickW(World w){
        gow(w);
    }
    public void ClickS(World w){
        gos(w);
    }
    public void ClickD(World w) {
        god(w);
    }
    public void ClickA(World w) {
        goa(w);
    }
    public void setPosition(Pair p){
    	position = p;
    }
    public void setVelocity(Pair v){
    	velocity = v;
    }
    public void setAcceleration(Pair a){
    	acceleration = a;
    } 
    public Pair getPosition(){
    	return position;
    }
    public Pair getVelocity(){
    	return velocity;
    }
    public Pair getAcceleration(){
    	return acceleration;
    } 
    public double flipX() {
    	acceleration.flipX();
	return 0.0;
    }
    public double flipY() {
    	acceleration.flipY();
	return 0.0;
    }
    public void draw(Graphics g){
    Color c = g.getColor();
     
    g.setColor(color);
    g.drawOval((int)(position.x - radius), (int)(position.y - radius), (int)(2*radius), (int)(2*radius));
    g.setColor(c);
    }
    
    private void bounce(World w){
    Boolean bounced = false;
    if (position.x - radius < 0){
        velocity.flipX();
        position.x = radius;
        bounced = true;
    }
    else if (position.x + radius > w.width){
        velocity.flipX();
        position.x = w.width - radius;
        bounced = true;
    }
    if (position.y - radius < 0){
        velocity.flipY();
        position.y = radius;
        bounced = true;
    }
    else if(position.y + radius >  w.height){
        velocity.flipY();
        position.y = w.height - radius;
        bounced = true;
    }
    if (bounced){
        velocity = velocity.divide(dampening);
    }
    }

    private void gow(World w){
        if (acceleration.y == 0) {
            acceleration.y = acceleration.x;
            acceleration.x = 0;
        }
        if (acceleration.y > 0) {
            acceleration.flipY();
        }
    }
    private void gos(World w){
        if (acceleration.y == 0) {
            acceleration.y = acceleration.x;
            acceleration.x = 0;
        }
        if (acceleration.y < 0) {
            acceleration.flipY();
        }
    }
    private void god(World w){
        if (acceleration.x == 0) {
            acceleration.x = acceleration.y;
            acceleration.y = 0;
        }
        if (acceleration.x < 0) {
            acceleration.flipX();
        }
    }
    private void goa(World w){
        if (acceleration.x == 0) {
            acceleration.x = acceleration.y;
            acceleration.y = 0;
        }
        if (acceleration.x > 0) {
            acceleration.flipX();
        }
    }
}
 
class World{
    int height;
    int width;
     
    int numSpheres;
    Sphere spheres[];
 
    public World(int initWidth, int initHeight, int initNumSpheres){
    width = initWidth;
    height = initHeight;
 
    numSpheres = initNumSpheres;
    spheres  = new Sphere[numSpheres];
     
    for (int i = 0; i < numSpheres; i ++)
        {
        spheres[i] = new Sphere();
        }
    }
 
    public void drawSpheres(Graphics g){
    for (int i = 0; i < numSpheres; i++){
        spheres[i].draw(g);
    }
    }
 
    public void updateSpheres(double time){
    for (int i = 0; i < numSpheres; i ++)
        spheres[i].update(this, time);
    }
    public void uSpheresW(){
        for (int i = 0; i < numSpheres; i ++)
            spheres[i].ClickW(this);
    }
    public void uSpheresS(){
        for (int i = 0; i < numSpheres; i ++)
            spheres[i].ClickS(this);
    }
    public void uSpheresD(){
        for (int i = 0; i < numSpheres; i ++)
            spheres[i].ClickD(this);
    }
    public void uSpheresA(){
        for (int i = 0; i < numSpheres; i ++)
            spheres[i].ClickA(this);
    }
}
 
public class TestingKeys extends JPanel implements KeyListener{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    World world;
 
    class Runner implements Runnable{
    public void run() {
        while(true){
        	world.updateSpheres(1.0 / (double)FPS);
        	repaint();
        try{
            Thread.sleep(1000/FPS);
        	}
        catch(InterruptedException e){}
        }
 
    }
     
    }
 
 
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
    }
    
    public void keyReleased(KeyEvent e) {
        char c=e.getKeyChar();
        System.out.println("You released: " + c);
        if (c == 'w') {
            world.uSpheresW();
        }
        if (c == 's') {
            world.uSpheresS();
        }
        if (c == 'd') {
            world.uSpheresD();
        }
        if (c == 'a') {
            world.uSpheresA();
        }
    }
 
 
    public void keyTyped(KeyEvent e) {
    	char c = e.getKeyChar();
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
 
    public TestingKeys(){
    world = new World(WIDTH, HEIGHT, 50);
    addKeyListener(this);
    this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    Thread mainThread = new Thread(new Runner());
    mainThread.start();
    }
     
    public static void main(String[] args){
    JFrame frame = new JFrame("Physics!!!");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    TestingKeys mainInstance = new TestingKeys();
    frame.setContentPane(mainInstance);
    frame.pack();
    frame.setVisible(true);
    }
 
 
    public void paintComponent(Graphics g) {
    super.paintComponent(g);        
 
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, WIDTH, HEIGHT);
 
    world.drawSpheres(g);
 
    }
 
     
}
