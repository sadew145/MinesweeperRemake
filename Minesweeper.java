package datastructure;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.*;


public class Minesweeper
{
	/*class variables here. */
	int[][] board;
	/*board size and number of mines. */
	private final int WIDTH = 10;
	private final int HEIGHT = 10;
	private final int NUM_MINES = 10;
	private boolean start;
	/*graphics */
	private JFrame frame;
	private JPanel mainPanel;
	private JLabel[][] labels;

	/* Sets up graphics stuff */
	public Minesweeper()
	{
		frame = new JFrame("Minesweeper");
		mainPanel = new JPanel();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		initialize();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.setLocationByPlatform(true);
		frame.pack();
		frame.setVisible(true);
		setUpVariables();
	}

	/* More graphics setup. */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Minesweeper();
			}
		});
	}
	
	/* Even more graphics setup */
	private void initialize()
	{
		mainPanel.setPreferredSize(new Dimension(500,500));
		mainPanel.setLayout(new GridLayout(WIDTH,HEIGHT));
		
		labels = new JLabel[HEIGHT][WIDTH];
		for (int i=0; i<HEIGHT; i++)
		{
			for (int j=0; j<WIDTH; j++)
			{
				labels[i][j] = new JLabel(" ", JLabel.CENTER);
				labels[i][j].setFont(new Font("Verdana", Font.BOLD, 24));
				labels[i][j].addMouseListener(new MouseHandler(i, j));
				labels[i][j].setOpaque(true);
				labels[i][j].setBackground(Color.BLACK);
				labels[i][j].setForeground(Color.GREEN);
				labels[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
				mainPanel.add(labels[i][j]);
			}
		}
	}

	/* This is called when the mouse is clicked */
	private class MouseHandler extends MouseAdapter
	{
		private int row;
		private int column;
		private boolean selected;
		
		public MouseHandler(int row, int column)
		{
			this.row = row;
			this.column = column;
			selected = false;
		}
		
		@Override
		public void mouseReleased(MouseEvent e) 
		{
			if (SwingUtilities.isRightMouseButton(e) || (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()))
			{
				selected = !selected;
				labels[row][column].setBackground(selected ? Color.ORANGE : Color.BLACK);
			}
			else if (SwingUtilities.isLeftMouseButton(e))
				handleClick(row, column);
		}
	}

	
	public void setUpVariables()
	{
		/* initialize class variables */		
		board= new int[WIDTH][HEIGHT];
		
	}
	
	public void setUpMines(int startRow, int startColumn)
	{
		/* randomly places NUM_MINES mines into the board */
		start=true;
		board=new int[WIDTH][HEIGHT];
		for (int[] row: board)
		    Arrays.fill(row, 0);
		Random rand = new Random();
		List<Map.Entry<Integer, Integer>> s = nbrs(startRow,startColumn);
		
		for (int x = 0; x <= NUM_MINES; x++)
		{
			int row = rand.nextInt(WIDTH);
			int column = rand.nextInt(HEIGHT);
			Entry<Integer, Integer> pair = new SimpleEntry<>(row,column);
			while (s.contains(pair)==true)
			{
				row = rand.nextInt(WIDTH);
				column = rand.nextInt(WIDTH);
				pair = new SimpleEntry<>(row,column);	
			}
			if (board[row][column] != 9)
				board[row][column] = 9;
			else
				while (board[row][column] != 9)
				{
					row = rand.nextInt(WIDTH);
					column = rand.nextInt(HEIGHT);
					while (column == startColumn && row == startRow)
					{
						row = rand.nextInt(WIDTH)+1;
						column = rand.nextInt(HEIGHT)+1;
					}
				}
			board[row][column] = 9;
		}
		
	}
	
	
	public void calculateNumbers()
	{
		/* calculates how many mines are adjacent to each cell */

		for(int i = 0 ; i<WIDTH ;i++)
			for(int j = 0 ; j<HEIGHT;j++)
				if(board[i][j]!=9)
					for (int ii = i - 1; ii <= i + 1; ii++)
	                    for (int jj = j - 1; jj <= j + 1; jj++)
	                    {
	                    	if(ii==i && jj==j)
	                    		continue;
	                    	else if (ii>-1 && ii<WIDTH && jj>-1 && jj<HEIGHT) 
	                    		if(board[ii][jj]==9 )
	                    			board[i][j]+=1;
	                    	else
	                    		continue;
	                    }
	}
	         

	
	public void handleClick(int row, int column)
	{
		/* determines what happens when the user clicks on a cell */
		if(board[row][column]==9)
		{
			labels[row][column].setText("B");
			JOptionPane.showMessageDialog(mainPanel,"game over"); 
			System.exit(0);
		}
		if(board[row][column]<9 && board[row][column]>=1)
		{
			labels[row][column].setText(Integer.toString(board[row][column]));
			gameWon();
		}
		if(board[row][column]==0)
		{
			if(start==false)
			{
				start=true;
				labels[row][column].setBackground(Color.WHITE);
				setUpMines(row,column);
				calculateNumbers();
				clearCells(row,column);
			}
			else
			{
				labels[row][column].setBackground(Color.WHITE);
				clearCells(row,column);
				gameWon();
			}
		}
	
	}
	
	public void clearCells(int startRow, int startColumn)
	{
		/* clears an entire area when the user clicks on a totally clear spot */
		Set<Pair> found = new LinkedHashSet<Pair>();
		Deque<Pair> waiting = new ArrayDeque<Pair>();
		found.add(new Pair(startRow,startColumn));
		waiting.add(new Pair(startRow, startColumn));
		while(!waiting.isEmpty())
		{
			Pair v = waiting.remove();
			for (int ii = v.x - 1; ii <= v.x + 1; ii++)
                for (int jj = v.y - 1; jj <= v.y + 1; jj++)
                {
                	if(ii==v.x && jj==v.y)
                		continue;
                	else if(ii>-1&& jj>-1 && ii<WIDTH && jj<HEIGHT)
                	{
                		Pair u = new Pair(ii,jj);
            			if(!found.contains(u) && v.x>=0 && v.y>=0 && v.x<WIDTH && v.y<HEIGHT)
            			{
            				if(board[u.x][u.y]==0)
            				{
            					found.add(u);
            					waiting.add(u);
            					labels[u.x][u.y].setBackground(Color.white);
            				}
            				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
            				{
            					found.add(u);
            					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
            				}
            				
            			}
                	}
                	else
                		continue;
                }
			/*
			Pair u = new Pair(v.x-1,v.y-1);
			if(!found.contains(u) && v.x>1 && v.y>1 && v.x<=WIDTH && v.y<=HEIGHT)
			{
				if(board[u.x][u.y]==0)
				{
					found.add(u);
					waiting.add(u);
					labels[u.x][u.y].setBackground(Color.white);
				}
				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
				{
					found.add(u);
					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
				}
			}
			u = new Pair(v.x,v.y-1);
			if(!found.contains(u) && v.x>1 && v.y>1 && v.x<=WIDTH && v.y<=HEIGHT)
			{
				if(board[u.x][u.y]==0)
				{
					found.add(u);
					waiting.add(u);
					labels[u.x][u.y].setBackground(Color.white);
				}
				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
				{
					found.add(u);
					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
				}
			}
			u = new Pair(v.x-1,v.y);
			if(!found.contains(u) && v.x>1 && v.y>1 && v.x<=WIDTH && v.y<=HEIGHT)
			{
				if(board[u.x][u.y]==0)
				{
					found.add(u);
					waiting.add(u);
					labels[u.x][u.y].setBackground(Color.white);
				}
				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
				{
					found.add(u);
					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
				}
			}
			u = new Pair(v.x+1,v.y-1);
			if(!found.contains(u) && v.x>1 && v.y>1 && v.x<=WIDTH && v.y<=HEIGHT)
			{
				if(board[u.x][u.y]==0)
				{
					found.add(u);
					waiting.add(u);
					labels[u.x][u.y].setBackground(Color.white);
				}
				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
				{
					found.add(u);
					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
				}
			}
			u = new Pair(v.x-1,v.y+1);
			if(!found.contains(u) && v.x>1 && v.y>1 && v.x<=WIDTH && v.y<=HEIGHT)
			{
				if(board[u.x][u.y]==0)
				{
					found.add(u);
					waiting.add(u);
					labels[u.x][u.y].setBackground(Color.white);
				}
				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
				{
					found.add(u);
					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
				}
			}
			u = new Pair(v.x+1,v.y);
			if(!found.contains(u) && v.x>1 && v.y>1 && v.x<=WIDTH && v.y<=HEIGHT)
			{
				if(board[u.x][u.y]==0)
				{
					found.add(u);
					waiting.add(u);
					labels[u.x][u.y].setBackground(Color.white);
				}
				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
				{
					found.add(u);
					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
				}
			}
			u = new Pair(v.x,v.y+1);
			if(!found.contains(u) && v.x>1 && v.y>1 && v.x<=WIDTH && v.y<=HEIGHT)
			{
				if(board[u.x][u.y]==0)
				{
					found.add(u);
					waiting.add(u);
					labels[u.x][u.y].setBackground(Color.white);
				}
				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
				{
					found.add(u);
					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
				}
			}
			u = new Pair(v.x+1,v.y+1);
			if(!found.contains(u) && v.x>1 && v.y>1 && v.x<=WIDTH && v.y<=HEIGHT)
			{
				if(board[u.x][u.y]==0)
				{
					found.add(u);
					waiting.add(u);
					labels[u.x][u.y].setBackground(Color.white);
				}
				else if (board[u.x][u.y]>0 && board[u.x][u.y]<9)
				{
					found.add(u);
					labels[u.x][u.y].setText(Integer.toString(board[u.x][u.y]));
				}
			}*/
		}
		
	}
	
	public List<Map.Entry<Integer, Integer>> nbrs(int startRow, int startColumn)
	{
		List<Map.Entry<Integer, Integer>> set = new ArrayList<Map.Entry<Integer, Integer>>();
		for (int p = startRow - 1; p <= startRow + 1; p++)
		{
			for (int q = startColumn - 1; q <= startColumn + 1; q++) 
			{
				Entry<Integer, Integer> pair = new SimpleEntry<>(p,q); 
				set.add(pair);
	        }
	    }
		return set;
	}
	
	private class Pair /* create an object which can hold 2 vars, easy to compare and easy to debug */
	{
		private int x;
		private int y;
		
		public String toString()
		{
			return "(" + x + "," + y + ")";
		}

		public Pair(int x, int y)
		{
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}	
	}
	
	public boolean gameWon()
	{
		/* determines if the game has been won or if it should continue. */
		int count=0;
		for(int i=0;i<WIDTH;i++)
			for(int j=0;j<HEIGHT;j++)
			{
				if(labels[i][j].getBackground()!=Color.ORANGE)
					count++;
			}
		if(count==WIDTH*HEIGHT-NUM_MINES)
		{
			JOptionPane.showMessageDialog(mainPanel,"You Won!"); 
			System.exit(0);
			return true;
		}
		return false;
	}
}