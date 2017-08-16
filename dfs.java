import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class dfs
{
	public Data data = new Data();

	public void controlRobot(IRobot robot) {
		if (robot.getRuns()==0 && Data.moves==0){
			data.setStartLocation(robot);
			data.setMazeData(robot.getMaze().getWidth(), robot.getMaze().getHeight());

		}
		data.increaseMoves();


		int walls = wallCount(robot);

		if (walls == 3){
			robot.face(deadEnd(robot));
		}else if(walls == 2){
			robot.face(corridor(robot));
		}else if (unexploredCount(robot)==0){
			robot.setHeading(data.getBacktrack());
		}else if (walls ==1){
			if (unexploredCount(robot) == 2) data.addNode(robot.getLocation(),robot);
			robot.face(randomDirection(robot));
		}else{
			if (unexploredCount(robot) == 3) data.addNode(robot.getLocation(),robot);
			robot.face(randomDirection(robot));
		}

		//data.drawBoard();
		data.checkCell(robot);
		data.paint();
	}

	public int corridor(IRobot robot){
		if (robot.look(IRobot.AHEAD) == IRobot.WALL){
			if (robot.look(IRobot.LEFT) == IRobot.WALL){
				return IRobot.RIGHT;
			}else{
				return IRobot.LEFT;
			}
		}return IRobot.AHEAD;
	}

	public int deadEnd(IRobot robot){
		return randomDirection(robot);
	}

	public int randomDirection(IRobot robot){
		if (unexploredChooser(robot)!=0) return unexploredChooser(robot);
		else{
			double randomNumber;
			int direction;
			do {
				randomNumber=Math.random();
				if 		(randomNumber<0.25) direction = IRobot.AHEAD;
				else if (randomNumber<0.50) direction = IRobot.LEFT;
				else if (randomNumber<0.75) direction = IRobot.RIGHT;
				else 						direction = IRobot.BEHIND;
			}while (robot.look(direction)==IRobot.WALL);
			return direction;
		}

	}

	public int unexploredChooser(IRobot robot){
		for (int i=0; i<4; i++)
			if (robot.look(IRobot.LEFT-i) == IRobot.PASSAGE) return IRobot.LEFT-i;
		return 0;
	}

	private int unexploredCount(IRobot robot){
		int z = 0;
		for (int i=0; i<4; i++)
			if (robot.look(IRobot.AHEAD+i) == IRobot.PASSAGE) z++;
		return z;
	}

	public int look(int direction, IRobot robot){
		int difference = robot.getHeading() - direction;
		if      (difference == 0)						return robot.look(IRobot.AHEAD);
		else if (difference == 2 || difference == -2)	return robot.look(IRobot.BEHIND);
		else if (difference == -3 || difference == 1)	return robot.look(IRobot.LEFT);
		else											return robot.look(IRobot.RIGHT);
	}

	private int wallCount(IRobot robot){
		int z = 0;
		for (int i=0; i<4; i++)
			if (robot.look(IRobot.AHEAD+i) == IRobot.WALL) z++;
		return z;
	}

	public void reset(){
		data.reset();
	}

}

class Data{
	// 0 = unvisited. 1 = wall. 2 = passage.
	private int[][] board;
	private boolean[][] visited;
	private int x,y;
	public JFrame window;
	public Point prevCord;
	public static Image mouseImage=null;
	public static Image catImage=null;
	final static int cubeX = 25, cubeY = 25;
	public static int moves;
	public Stack<Point> stack;
	public Stack<Integer> backtrack;

	public Data(){
		moves=0;
		stack = new Stack<Point>();
		backtrack =new Stack<Integer>();
		try{
			mouseImage = ImageIO.read(new File(".\\mouse.png"));
			catImage = ImageIO.read(new File (".\\cat.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addNode(Point p, IRobot robot){
		stack.add(p);
		addToBacktrack(robot);
	}

	public void addToBacktrack(IRobot robot){
		int heading = robot.getHeading();
		switch (heading){
			case IRobot.NORTH:backtrack.add(IRobot.SOUTH);break;
			case IRobot.EAST:backtrack.add(IRobot.WEST);break;
			case IRobot.SOUTH:backtrack.add(IRobot.NORTH);break;
			default:backtrack.add(IRobot.EAST);break;
		}
	}

	public int getBacktrack(){
		return (Integer) backtrack.pop();
	}

	public void setMazeData(int x, int y){
		this.x=x-2;
		this.y=y-2;
		board = new int[x-2][y-2];
		visited = new boolean[x-2][y-2];

		//Creating the frame.
		window = new JFrame("Maze");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setBounds(0, 0, (this.x+2)*cubeX+15, (this.y+2)*cubeY+38);
		window.setVisible(true);
	}

	public void setStartLocation(IRobot robot){
		if (prevCord == null){
			prevCord=robot.getLocation();
		}
	}

	public void increaseMoves(){
		moves++;
	}

	public void reset(){
		moves = 0;
		board = new int[x][y];
		visited = new boolean[x][y];
		prevCord = new Point(1,1);
	}

	public void checkCell(IRobot robot){
		updateMouse(robot);
		if (visited[robot.getLocation().x-1][robot.getLocation().y-1]){
			//System.out.println("I've been here before.");
			return;
		}else{
			visited[robot.getLocation().x-1][robot.getLocation().y-1] = true;
			fillBoard(robot);
			//System.out.println("I haven't been here before.");
		}
	}

	public void updateMouse(IRobot robot){
		board[prevCord.x-1][prevCord.y-1]=4;
		prevCord = robot.getLocation();
		board[robot.getLocation().x-1][robot.getLocation().y-1]=3;
		board[robot.getTargetLocation().x-1][robot.getTargetLocation().y-1]=5;
	}

	public void fillBoard(IRobot robot){
		if (robot.getLocation().y != 1){
			if (look(IRobot.NORTH,robot) != IRobot.WALL){
				board[robot.getLocation().x-1][robot.getLocation().y-2] = 2;
			}else{
				board[robot.getLocation().x-1][robot.getLocation().y-2] = 1;
			}
		}

		if (robot.getLocation().x != x){
			if (look(IRobot.EAST,robot) != IRobot.WALL){
				board[robot.getLocation().x][robot.getLocation().y-1] = 2;
			}else{
				board[robot.getLocation().x][robot.getLocation().y-1] = 1;
			}
		}

		if (robot.getLocation().x != 1){
			if (look(IRobot.WEST,robot) != IRobot.WALL){
				board[robot.getLocation().x-2][robot.getLocation().y-1] = 2;
			}else{
				board[robot.getLocation().x-2][robot.getLocation().y-1] = 1;
			}
		}

		if (robot.getLocation().y != y){
			if (look(IRobot.SOUTH,robot) != IRobot.WALL){
				board[robot.getLocation().x-1][robot.getLocation().y] = 2;
			}else{
				board[robot.getLocation().x-1][robot.getLocation().y] = 1;
			}
		}
	}

	public int look(int direction, IRobot robot){
		int difference = robot.getHeading() - direction;
		if      (difference == 0)						return robot.look(IRobot.AHEAD);
		else if (difference == 2 || difference == -2)	return robot.look(IRobot.BEHIND);
		else if (difference == -3 || difference == 1)	return robot.look(IRobot.LEFT);
		else											return robot.look(IRobot.RIGHT);
	}

	public void drawBoard(){

		System.out.println();
		for(int i=-1; i <= x*2; i++){
			System.out.print("_");
		}
		for (int i=0;i < y;i++){
			System.out.println();
			System.out.print("|");

			char c = 'a';


			for (int z=0; z<x; z++){
				if (board[z][i] == 0) c='~';
				else if (board[z][i] == 1) c='#';
				else c=' ';
				System.out.print(c +" ");
			}
			System.out.print("|");
		}

		System.out.println();
		for(int i=-1; i <= x*2; i++){
			System.out.print("_");
		}

	}


	public void paint(){
		window.getContentPane().removeAll();
		window.add(new MazeC(board,window));
		window.revalidate();
		window.repaint();
	}

}

class MazeC extends JComponent{

	int[][] array;
	JFrame window;

	public MazeC(int[][] arr, JFrame window){
		array = arr;
		this.window = window;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.fillRect (0, 0, Data.cubeX*(array.length+2), Data.cubeY*(2+array.length));
		g.setColor(Color.magenta);
		g.fillRect(0,0,Data.cubeX*(array.length+2),Data.cubeY);
		g.fillRect(0,Data.cubeX*(array.length+1),Data.cubeX*(array.length+2),Data.cubeY);
		g.fillRect(0,0,Data.cubeX,Data.cubeY*(2+array.length));
		g.fillRect(Data.cubeX*(array.length+1),0,Data.cubeX,Data.cubeY*(2+array.length));
		//0 unexplored
		//1 wall
		//2 passage
		//3 mouse
		//4 explored
		//5 goal
		for (int y = 0; y<array.length; y++){
			for (int x=0; x<array.length; x++){
				switch (array[y][x]){
					case 0: g.setColor(Color.black);	g.fillRect (Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX);break;
					case 1: g.setColor(Color.blue);		g.fillRect (Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX);break;
					case 3: g.drawImage(Data.catImage, Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX, this);		break;
					case 5: g.drawImage(Data.mouseImage, Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX, this);		break;
					default:g.setColor(Color.white);	g.fillRect (Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX);
				}
			}
		}
	}

}
