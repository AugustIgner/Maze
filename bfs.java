import uk.ac.warwick.dcs.maze.logic.IRobot;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class bfs
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
		}//FINISH

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

	public int crossRoads(IRobot robot){

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
	private int x,y;
	private int[][] board;
	private int[][] nodes;
	public static int moves;

	private Point prevCord;
	private int prevState;

	private JFrame window;
	public static Image mouseImage=null;
	public static Image catImage=null;
	final static int cubeX = 5, cubeY = 5;


	public Data(){
		moves=0;
		try{
			mouseImage = ImageIO.read(new File("C:\\Users\\AI\\Documents\\java\\mouse.png"));
			catImage = ImageIO.read(new File ("C:\\Users\\AI\\Documents\\java\\cat.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
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
			board[robot.getTargetLocation().x-1][robot.getTargetLocation().y-1]=4;
			prevCord=robot.getLocation();
			prevState=2;
		}
	}

	public void checkNode(IRobot robot){

	}

	public void increaseMoves(){
		moves++;
	}

	public void reset(){
		moves = 0;
		board = new int[x][y];
		nodes = new int[x][y];
		prevCord = new Point(1,1);
	}

	public void checkCell(IRobot robot){
		updateMouse(robot);
		if (nodes[robot.getLocation().x-1][robot.getLocation().y-1]==0){
			return;
		}else{
			fillBoard(robot);
		}
	}

	public void updateMouse(IRobot robot){
		board[prevCord.x-1][prevCord.y-1]=prevState;
		prevCord = robot.getLocation();
		prevState = board[robot.getLocation().x-1][robot.getLocation().y-1];
		board[robot.getLocation().x-1][robot.getLocation().y-1]=3;
	}


/*
	Check if undiscovered.
		if it is discovered check whether it is a node
			if a node
			Hello

*/












		//0 unexplored
		//1 wall
		//2 corridor node
		//3 mouse
		//4 cat
		//5 mouse
	public void fillBoard(IRobot robot){

		int walls = bfs.wallCount();

		if (robot.getLocation().y != 1){
			if (look(IRobot.NORTH,robot) == IRobot.WALL){
				board[robot.getLocation().x-1][robot.getLocation().y-2] = 1;
			}else{
				if (walls >=2){
					board[robot.getLocation().x-1][robot.getLocation().y-2] = 2;
				/*}*/
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
		//2 corridor node
		//3 cat
		//4 mouse
		//
		for (int y = 0; y<array.length; y++){
			for (int x=0; x<array.length; x++){
				switch (array[y][x]){
					case 0: g.setColor(Color.black);	g.fillRect (Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX);break;
					case 1: g.setColor(Color.blue);		g.fillRect (Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX);break;
					case 3: g.drawImage(Data.catImage, Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX, this);		break;
					case 4: g.drawImage(Data.mouseImage, Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX, this);		break;
					default:g.setColor(Color.white);	g.fillRect (Data.cubeY*(y+1), Data.cubeX*(x+1), Data.cubeY, Data.cubeX);
				}
			}
		}
	}

}
