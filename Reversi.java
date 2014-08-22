import java.util.*;
import java.io.*;
/*
* Name: Jaden Chen
* 
* Reversi/Othello Game (Console Version) 
* On-Line-School
* ICS 3UZ - Computer Science
* Date:11/05/12
* 
* This game is my choice for the major project.
* Reversi is interactive board game for two players. Each players goal is to get more pieces on the board than their opponent. 
* Rules and game mechanics are explained in internal documentation as well as tutorial/user manual.
* This program is very thorough covering all possible end-game possibilities and following the game mechanics to perfection.
* Additional Features: AI players (you get to choose which players are AI), board size change (you get to set dimensions for your board, allowing the board to have a rectangular shape).
* A thorough tutorial is also included.
* Feel free to experiment.
*/
public class Reversi{
//initializing static variables
//boardlength and boardwidth to be used in multiple methods to designate the board's width and length.
static int boardlength=8,boardwidth=8;
//this one is self-explanatory it is the array that stores the game board, used in virtually all parts of the program
static String board[][]=new String[boardwidth+3][boardlength+3];
//dynamic board is used to record points in the conversion process (later explained, when the game looks into diagonal directions, this is used to record values to be changed)
static int dynamicboard[][]=new int[boardwidth+3][boardlength+3];
//player's score will be used in multiple methods.
static int player1score, player2score;
//These booleans have much to do with checking (if a move is valid, if a player can make any move on the entire board, if a ending condition is met(both players cannot move anymore))
static boolean validation=false;
static boolean boardavailability=false;
//will tell the program if a player is ai or not.
static boolean player1ai=false;
static boolean player2ai=false;
static int nomovecount=0;

	public static void main(String[]args)throws IOException{
		//initializing scanner and random number generator
		Scanner scan = new Scanner(System.in);
		Random generator = new Random();
		System.out.println("Welcome to a game of Reversi/Othello.\nChoose option 3 in the main menu if you do not know how to play the game.");
		System.out.println("Press any key to proceed to menu...");
		System.in.read();
		System.out.println("\t\t Main Menu");
		System.out.println("\t\t *********");
        System.out.println("\t\t 1. Play Reversi");
        System.out.println("\t\t 2. Options");
        System.out.println("\t\t 3. How to play");
        System.out.println("\t\t 4. Exit");
        int choice=scan.nextInt();
        while(choice<1||choice>4){
        	System.out.println("Invalid option. Try again.");
        	choice=scan.nextInt();
        }
        while(choice==1||choice==2||choice==3){
        	if(choice==1){
        		//creating game board
        		for(int x=0;x<boardwidth+2;x++){
        			for(int i=0;i<boardlength+2;i++){
        				board[x][i]="-";
        				dynamicboard[x][i]=0;
        			}
        		}
        		//assigning starting pieces
        		board[boardwidth/2][boardlength/2]="x";
        		board[boardwidth/2+1][boardlength/2+1]="x";
        		board[boardwidth/2+1][boardlength/2]="o";
        		board[boardwidth/2][boardlength/2+1]="o";
        		playerpoints(board);
        		int movechoice;
        		//this variable will alternate every loop to give turns to both player 
        		int playerturn=1;
        		//piecevalue sets the value of a piece depending on the player turn, if player 1, then piecevalue will be x, if player 2, it will be o. Will be used in following loop
        		String piecevalue="x";
        		//fullboard and playerlost are boolean variables that monitor the state of the board, game will end if the board is full or if a player has lost (has no more pieces left standing on the board)
        		boolean fullboard=false;
        		boolean playerlost=false;
        		//preliminary fullboard check (if the dimensions are set at 2 by 2, game will automatically end at the start of the game since the board is ALREADY full)
        		fullboard=true;
    			for(int x=1;x<=boardwidth;x++){
					for(int i=1;i<=boardlength;i++){
						if(board[x][i].equals("-")){
							fullboard=false;
						}
					}
				}
    			//while loop begins, this loop will end once an end condition has been met
    			//end conditions are explained in the tutorial or user manual but essentially, the game ends when the board is full, a player has no more pieces on the board left or both players cannot make a move.
        		while(fullboard==false&&playerlost==false&&nomovecount!=2){
        			//boolean is first set to false and a check will be performed to see if the ai has been enabled for this player.
        			boolean ai=false;
        			//checks this for player 1
        			if(playerturn==1){
        				if(player1ai==true){
        					ai=true;
        				}
        			}
        			//checks player 2
        			if(playerturn==2){
        				if(player2ai==true){
        					ai=true;
        				}
        			}
        			//assigning the piece value according to the player's turn. Player 1=x Player 2=o
        			if(playerturn==1){
        			piecevalue="x";
        			}
        			else if(playerturn==2){
        			piecevalue="o";
        			}
        			//checks board to see if ANY move can be made during this turn, if no move can, this move will be automatically skipped.
        			boardcheck(piecevalue);
        			//in the case that ai is not enabled for this player, the program will let the user enter values (choose their move)
        			if(ai==false){
        				//display player turn and current score for both players
	        			System.out.println("\nPlayer "+playerturn+"'s turn ("+piecevalue+")       Score - Player 1: "+player1score+" points (x), Player 2: "+player2score+" points (o)");
	        			//displaying game board so the player can decide on their next move
	        			displayboard(board);
	        			//the following block of code will be initialized if boardcheck method has determined that a move can be made during this turn.
	        			if(boardavailability==true){
	        				//player gets to choose if they want to pass or make a move. Once a player wants to make a move it is too late to pass. 
	        				//However, this issue is minor since no player would want to pass their move and if they do want to, they should plan ahead and press pass.
	        				//Remember, if no moves can be made, this block of code would not even be initialized, the program would auto-skip the player's turn, therefore there will always be a move to make.
	        				System.out.println("\n(1)Make a move (2)Pass");
	        				movechoice=scan.nextInt();
	        				while(movechoice!=1&&movechoice!=2){
	        					System.out.println("Invalid option, try again.");
	        					movechoice=scan.nextInt();
	        				}
	        				//if the player chooses to make a move, this block of code is initialized
	        				if(movechoice==1){
	        					//will keep looping and asking user to enter a row and column until valid coordinates are entered
	        					while(validation==false){
	        						int row, column;
	        						System.out.print("Enter the row:");
	        						row=scan.nextInt();
	        						//a player cannot put a piece thats out of the range of the board, this prevents them from doing so
	        						while(row>boardlength||row<1){
	        							System.out.println("Invalid row on the board. Try again.");
	        							row=scan.nextInt();
	        						}
	        						//a player cannot put a piece thats out of the range of the board, this prevents them from doing so
	        						System.out.print("Enter the column:");
	        						column=scan.nextInt();
	        						while(column>boardlength||column<1){
	        							System.out.println("Invalid column on the board. Try again.");
	        							column=scan.nextInt();
	        						}
	        						//validationcheck method checks to see if the coordinate that the players wants to place a piece on is -, if it is, then validation will become true.
	        						//players cannot place a piece on an area of the board that has already been taken
	        						validation=validationcheck(row,column,piecevalue);
	        						//if the first validation method is true, it will then check to see if placing a piece on these coordinates will perform an action, if not, validation is still false and the loop will continue...
	        						if(validation==true){
		        						validationcheck2(row,column,piecevalue);
		        					}
	        						//in the case that validation is true after both checks, then the program will proceed to place a piece on the specified coordinate and perform the resulting actions (see tutorial or user manual if you do not know how this game works)
	        						if(validation==true){
	        							board[row][column]=piecevalue;
	        							changepieces(row,column,piecevalue);
	        						}
	        						//naturally if validation is false, the program will simply display a message telling the user that it is an invalid move. It will then loop again and ask for a row and column again.
	        						else if(validation==false){
	        							System.out.println("Invalid move, try again.");
	        						}
	        					}
	        					//necessary or validation will be true on next loop skipping the inside loop.
	        					validation=false;
	        				}
	        				//if player skips, no action is performed as shown in this else if statement
	        				else if(movechoice==2){
	        					System.out.println("Player "+playerturn+" has chosen to pass their turn.");
	        				}
	        				//checks if the board is full, if it is then the game will end.
	        				fullboard=true;
	        				for(int x=1;x<=boardwidth;x++){
	        					for(int i=1;i<=boardlength;i++){
	        						if(board[x][i].equals("-")){
	        							fullboard=false;
	        						}
	        					}
	        				}
	        				//switches playerturn so that the next loop, the other player gets a turn.
	        				if(playerturn==1){
	        					playerturn=2;
	        				}
	        				else{
	        					playerturn=1;
	        				}
	        				//calculates the amount of points each player has (which will be displayed in the next loop or if the game ends
	        				playerpoints(board);
	        				if(player1score==0||player2score==0){
	        					playerlost=true;
	               				}
	        			}
	        			//the following block of code will be initialized if the boardcheck method has determined that no moves can be made so it will skip the player's turn.
	        			else{
	        				System.out.println("Auto-Pass. This turn has been skipped due to the fact that this player cannot make any moves in this turn.");
	        				System.out.println("Press any key to continue to the next turn...");
	        				//instead of skipping too immediately and making the user lose track of what happened, the user gets to see that their move has been skipped.
	        				System.in.read();
	        				//notice how the fullboard method check and playerpoints method have not been initialized here. This is because the player's turn has been skipped so the points and the board will not change.
	        				if(playerturn==1){
	            				playerturn=2;
	            			}
	            			else{
	            				playerturn=1;
	            			}
	        			}
        			}
        			//the following block of code is the similar to the if statement in which there is no ai.
        			//since there is AI, the user will not be asked to enter row/column and the program will randomly generator numbers until a valid move is generated and that will be used as the row and column.
        			else{
        				System.out.println("\nPlayer "+playerturn+"'s turn ("+piecevalue+")       Score - Player 1: "+player1score+" points (x), Player 2: "+player2score+" points (o)");
	        			displayboard(board);
	        			System.out.println("\n(AI player turn) Press any NUMBER to continue to the next turn...");
	        			//System.in.read(); does not work properly in this situation (it may be skipped due to some reason)
	        			//scan.nextInt(); will always work
	        			int read=scan.nextInt();
	        			if(boardavailability==true){
	        				//chosenrow and chosencolumn variables
	        				int chosenrow=0,chosencolumn=0;
	        					while(validation==false){
	        						int row, column;
	        						//generator random row and columns, these can be invalid and if so, the program will just loop this part again until valid numbers are generated.
	        						row=generator.nextInt(boardwidth)+1;
	        						column=generator.nextInt(boardlength)+1;
	        						validation=validationcheck(row,column,piecevalue);
	        						if(validation==true){
	        							validationcheck2(row,column,piecevalue);
	        						}
	        						if(validation==true){
	        							board[row][column]=piecevalue;
	        							changepieces(row,column,piecevalue);
	        							chosenrow=row;
	        							chosencolumn=column;
	        						}
	        					}
	        				//displaying the coordinates that the AI chose.
	        				System.out.println("Player "+playerturn+" has placed a piece on coordinates ("+chosenrow+","+chosencolumn+")\n");
	        				validation=false;
	        				fullboard=true;
	        				for(int x=1;x<=boardwidth;x++){
	        					for(int i=1;i<=boardlength;i++){
	        						if(board[x][i].equals("-")){
	        							fullboard=false;
	        						}
	        					}
	        				}
	        				if(playerturn==1){
	        					playerturn=2;
	        				}
	        				else{
	        					playerturn=1;
	        				}
	        				playerpoints(board);
	        				if(player1score==0||player2score==0){
	        					playerlost=true;
	               			}
	        			}
	        			//the AI's move will also be skipped if no moves can be made on the turn.
	        			else{
	        				System.out.println("Auto-Pass. This turn has been skipped due to the fact that this player cannot make any moves in this turn.");
	        				System.out.println("Press any key to continue...");
	        				System.in.read();
	        				if(playerturn==1){
	            				playerturn=2;
	            			}
	            			else{
	            				playerturn=1;
	            			}
	        			}
        			}
        			//nomovecheck method checks both players to see if they can make a move on the current board, if both of them can't, then the game will end.
        			nomovecheck();
        			//boardavailability will be reset to false and further changes on this boolean will be set in the next loop
        			//this also ensures that if the game ends and restarts, this value will be false and not cause the game to end immediately.
        			boardavailability=false;
        		}
        		//game ends if the above while loop ends (due to a certain condition broken)
        		displayboard(board);
        		//displays board and score of each player as well as a message of congratulations
        		System.out.println("\nFinal score - Player 1: "+player1score+" points, Player 2: "+player2score+" points");
        		if(player1score>player2score){
        			System.out.println("\nCongratulations Player 1. You are the winner! Better luck next time Player 2 :(");
        		}
        		else if(player2score>player1score){
        			System.out.println("\nCongratulations Player 2. You are the winner! Better luck next time Player 1 :(");
        		}
        		else if(player1score==player2score){
        			System.out.println("\nIt's a tie! Congratulations to both players!");
        		}
        		//player scores are reset to 2 (starting game scores), nomovecount is set to 0 in case the user decides to run another game.
        		player1score=2;
        		player2score=2;
        		//if nomovecount is not reset to 0, the next game will end immediately as it starts
        		nomovecount=0;
        	}
        	else if(choice==2){
        		//Options choice, allows users to modify board dimensions and set AI players.
        		int optionchoice;
        		System.out.println("What would you like to modify? (1)Board Dimensions (2)Set AI players");
        		optionchoice=scan.nextInt();
        		while(optionchoice!=1&&optionchoice!=2){
        			System.out.println("Invalid option, try again.");
        			optionchoice=scan.nextInt();
        		}
        		//lets user change board size
        		if(optionchoice==1){
        			System.out.println("Here you'll be able to change the reversi gamem board size to your liking.");
        			System.out.print("Assign the length of the board: ");
        			boardlength=scan.nextInt();
        			//having boardlength or boardwidth be less than 2, the fundamentals mechanics of Reversi will be broken.
        			//if boardlength is over 9, then the column indicators will be inaccurate (after 9, 10 is double digit and will cause the column indicators to not line up to the game board anymore)
        			while(boardlength<2||boardlength>9){
        				System.out.println("The game cannot support this board length breaks. Try again.");
        				boardlength=scan.nextInt();
        			}
        			System.out.print("Assign the width of the board: ");
        			boardwidth=scan.nextInt();
        			//there is no reason why the width should be less than 9, but keeping it to maximum dimensions of 9 by 9 board is more professional.
        			while(boardwidth<2||boardwidth>9){
        				System.out.println("The game cannot support this board width breaks. Try again.");
        				boardwidth=scan.nextInt();
        			}
        			System.out.println("Your board now has dimensions: "+boardlength+" by "+boardwidth);
        		}
        		//Here, the user will be able to set AI players, all 4 possibilities are listed.
        		else if(optionchoice==2){
        			int aichange;
        			System.out.println("(1)Player vs. Player (2)Player vs. Ai (3)AI vs. AI (4)AI vs. Player");
        			aichange=scan.nextInt();
        			while(aichange<1||aichange>4){
        				System.out.println("Invalid option, try again.");
        				aichange=scan.nextInt();
        			}
        			if(aichange==1){
        				player1ai=false;
        				player2ai=false;
        				}
        			else if(aichange==2){
        				player1ai=false;
        				player2ai=true;
        				}
        			else if(aichange==3){
        				player1ai=true;
        				player2ai=true;
        				}
        			else if(aichange==4){
        				player1ai=true;
        				player2ai=false;
        			}
        		}
        	}
        	//Game tutorial, explains the game using the actual game board to effectively teach the user how the game works
        	else if(choice==3){
        		boardwidth=8;
        		boardlength=8;
        		for(int x=0;x<boardwidth+2;x++){
        			for(int i=0;i<boardlength+2;i++){
        				board[x][i]="-";
        			}
        		}
        		board[boardwidth/2][boardlength/2]="x";
        		board[boardwidth/2+1][boardlength/2+1]="x";
        		board[boardwidth/2+1][boardlength/2]="o";
        		board[boardwidth/2][boardlength/2+1]="o";
        		System.out.println("Reversi Game Tutorial");
        		System.out.println("\nPress any key to continue...");
        		System.in.read();
        		System.out.println("Reversi is a board game where each player's aim is to have more pieces on the board.");
        		System.out.println("Turns alternate and on each player's turn, the player can make a move by placing their pieces somewhere on the board.\nAlternatively, they can pass(not recommended)");
        		System.out.println("Please read on to find out more about the pass feature.");
        		System.out.println("\nPress any key to continue...");
        		//double System.in.read(); are used as one does not perform the action somehow
        		System.in.read();
        		System.in.read();
        		displayboard(board);
        		System.out.println("\nThis is the starting board for most games, each player will have 2 pieces diagonal to each other at the center of the board");
        		System.out.println("[x] are pieces of player 1, [o] are pieces of player 2, [-] represent unused spaces");
        		System.out.println("\nPress any key to continue...");
        		System.in.read();
        		System.in.read();
        		System.out.println("When a piece is placed, it'll convert [up, down, left, right, all diagonals directions] pieces to its own type if a specific condition is met.");
        		System.out.println("The condition is that there must be a same-type piece in that certain direction.\nSo, if this condition is met, all enemy pieces in between the placed piece and the piece it is making a 'connection' with.\nNOTE:This connection is broken if there is a blank space [-] in between the connection.");
        		System.out.println("\nPress any key to continue..."); 
        		System.in.read();
        		System.in.read();
        		System.out.println("Example: In this situation, Player 1 (x) decides to place their piece on coordinates (5,3)");
        		displayboard(board);
        		board[5][3]="x";
        		board[5][4]="x";
        		System.out.println("\nPress any key to continue..."); 
        		System.in.read();
        		System.in.read();
        		displayboard(board);
        		System.out.println("\nHere is the result of this move.");
        		System.out.println("This can happen with all directions simultaneously as long as the above condition is met.");
        		System.out.println("\nPress any key to continue..."); 
        		System.in.read();
        		System.in.read();
        		System.out.println("There a few rules to the placing of these pieces on a player's turn:");
        		System.out.println("Rule #1: the player piece must be placed on a blank space represented by [-] (cannot replace an already placed piece such as [x] or [o])");
        		System.out.println("Rule #2: the player's move must perform an action (make a 'connection' with another piece) other than being placed on the board.");
        		System.out.println("NOTE: Rule #2 will take some time to comprehend. Example: a unit cannot be placed in an area where its surrounding units are blank [-],\nremember, a blank space breaks/blocks potential connection.");
        		System.out.println("\nPress any key to continue..."); 
        		System.in.read();
        		System.in.read();
        		System.out.println("ILLEGAL MOVES");
        		board[7][7]="o";
        		board[3][3]="x";
        		displayboard(board);
        		System.out.println("\nCoordinates (7,7) and (3,3) are illegal in this scenario.\n(7,7) cannot make any connections due to being surrounded by blank space, on top of the fact that even if it was not, its (up,down,left,right,diagonals) directions never reach the other two present [o] units.\n(3,3) does reach another x at (down,right) direction, but NO connection is made. A connection must involve opponent pieces in between friendly pieces.");
        		System.out.println("\nPress any key to continue..."); 
        		System.in.read();
        		System.in.read();
        		System.out.println("Do you need to worry about this? No, the program will not let you make any illegal moves breaking the rules.\nYou will need to re-enter your move if this happens.");
        		System.out.println("In addition, if no move can be made during one's turn, the program will automatically skip this turn at your leisure.");
        		System.out.println("\nPress any key to continue..."); 
        		System.in.read();
        		System.in.read();
        		System.out.println("Conditions of winning:\n1.Board is full, player with most points wins\n2.One player has no units/pieces left standing, their opponent wins\n3.No moves can be made by either player, player with most points win");
        		System.out.println("\nFor more information, see the user manual.\nEnjoy your game!");
        		System.in.read();
        	}
        	System.out.println("\nPress any key to return to menu...");
        	System.in.read();
        	//For some reasons, System.in.read(); does not work after a game (choice 1) has been played. This if statement ensures that it does.
        	if(choice==1){
        		System.in.read();
        	}
        	//re-initializing menu
        	System.out.println("\t\t Main Menu");
    		System.out.println("\t\t *********");
            System.out.println("\t\t 1. Play Reversi");
            System.out.println("\t\t 2. Options");
            System.out.println("\t\t 3. How to play");
            System.out.println("\t\t 4. Exit");
            choice=scan.nextInt();
		}
        System.out.println("Thank you for playing Reversi, come play again anytime.");
	}
	public static void displayboard(String board[][]){
		//this method displays the board with column and row indicators (that line up with the rows and columns to help the user indicate what the coordinates are.
		//displays board in accordance to the assigned width and length of the board
		int rowindicator[]=new int[boardwidth+1];
		int rowcount=1;
		for(int i=1;i<=boardwidth;i++){
			rowindicator[i]=rowcount;
			rowcount++;
		}
		int columnindicator[]=new int[boardlength+1];
		int columncount=1;
		for(int i=1;i<=boardlength;i++){
			columnindicator[i]=columncount;
			columncount++;
		}
		System.out.print("\n  ");
		for(int i=1;i<=boardlength;i++){
			//displays column indicator
			System.out.print(" "+columnindicator[i]);
		}
		//displays board
		System.out.println("");
		for(int x=1;x<=boardwidth;x++){
			//displays row indicator
			System.out.print(" "+rowindicator[x]);
			for(int i=1;i<=boardlength;i++){
				System.out.print(" "+board[x][i]);
			}
			System.out.println();
		}
	}
	//this method is used to check EVERY space on the board that the current player can place a piece on. If none are found, then validation will become false and the player's turn will be skipped.
	public static void boardcheck(String piecevalue){
		//in all check methods, an opposite piece value must be set which holds the opposite piece to the current one (ie. x's opposite piece is o, o's opposite piece is x)
		String oppositepiece;
		if(piecevalue.equals("x")){
			oppositepiece="o";
		}
		else if(piecevalue.equals("o")){
			oppositepiece="x";
		}
		//This step is needed or JAVA will think that the piecevalue may not have been initialized
		else{
			oppositepiece="-";
		}
		//With the 2 for loops and the if statement, the program will go through the whole board but only check the value if it is on a blank space [-].
		for(int row=1;row<=boardwidth;row++){
			for(int column=1;column<=boardlength;column++){
				if(board[row][column].equals("-")){
					//found booleans will turn into true if a 'connection' is available and an action can be made with the current row/column in the loop. 
					boolean foundR=false,foundL=false,foundT=false,foundB=false,foundTL=false,foundTR=false,foundBL=false,foundBR=false;
					//checkfurther all begin as 1 and will increase in value if an opposite piece is found in the direction it is checking, this lets the while loop continue and check more in that direction.
					int checkfurtherR=1,checkfurtherL=1,checkfurtherT=1,checkfurtherB=1,checkfurtherTL=1,checkfurtherTR=1,checkfurtherBL=1,checkfurtherBR=1;
					//R=right, L=left, T=top, B=bottom, and there are diagonals such as TL=top left.
					//program will check all directions using this scheme.
					//If the board in this direction holds and opposite piece, it will advance and check further, until something other than an opposite piece is reached.
					while(board[row][column+checkfurtherR].equals(oppositepiece)){
						checkfurtherR++;
					}
					//If the piece after the check furthers have stopped is the same-type piece, then everything in between will be converted to the current piece. A 'connection' is made as explained in the tutorial/user manual.
					if(board[row][column+checkfurtherR].equals(piecevalue)&&checkfurtherR>1){
						foundR=true;
					}
					while(board[row][column-checkfurtherL].equals(oppositepiece)){
						checkfurtherL++;
					}
					if(board[row][column-checkfurtherL].equals(piecevalue)&&checkfurtherL>1){
						foundL=true;
					}
					while(board[row+checkfurtherB][column].equals(oppositepiece)){
						checkfurtherB++;
					}
					if(board[row+checkfurtherB][column].equals(piecevalue)&&checkfurtherB>1){
						foundB=true;
					}
					while(board[row-checkfurtherT][column].equals(oppositepiece)){
						checkfurtherT++;
					}
					if(board[row-checkfurtherT][column].equals(piecevalue)&&checkfurtherT>1){
						foundT=true;
					}
					while(board[row-checkfurtherTR][column+checkfurtherTR].equals(oppositepiece)){
						checkfurtherTR++;
					}
					if(board[row-checkfurtherTR][column+checkfurtherTR].equals(piecevalue)&&checkfurtherTR>1){
						foundTR=true;
					}
					while(board[row-checkfurtherTL][column-checkfurtherTL].equals(oppositepiece)){
						checkfurtherTL++;
					}
					if(board[row-checkfurtherTL][column-checkfurtherTL].equals(piecevalue)&&checkfurtherTL>1){
						foundTL=true;
					}
					while(board[row+checkfurtherBL][column-checkfurtherBL].equals(oppositepiece)){
						checkfurtherBL++;
					}
					if(board[row+checkfurtherBL][column-checkfurtherBL].equals(piecevalue)&&checkfurtherBL>1){
						foundBL=true;
					}
					while(board[row+checkfurtherBR][column+checkfurtherBR].equals(oppositepiece)){
						checkfurtherBR++;
					}
					if(board[row+checkfurtherBR][column+checkfurtherBR].equals(piecevalue)&&checkfurtherBR>1){
						foundBR=true;
					}
					//If any pieces have
					if(foundR==true||foundL==true||foundT==true||foundB==true||foundTL==true||foundTR==true||foundBL==true||foundBR==true){
						boardavailability=true;
					}
				}
			}
		}
		//This if statement does not have to do with the current method. It is useful to the next nomovecheck method.
		if(boardavailability==false){
			nomovecount++;
		}
	}
	//nomovecheck method checks both players to see if they can make a move on the current board, if both of them can't, then the game will end.
	public static void nomovecheck(){
		nomovecount=0;
		boardcheck("x");
		boardcheck("o");
	}
	//First validation check, checks to see if the pending coordinate is being placed on a [-] space. If it's not, the validation will resutl in false;
	public static boolean validationcheck(int row, int column, String piecevalue){
		if(board[row][column].equals("x")||board[row][column].equals("o")){
			return false;
		}
		else{
			return true;
		}
	}
	//Same as the boardcheck method but checks only a specific row and column to find out if its valid or not.
	public static void validationcheck2(int row, int column, String piecevalue){
		String oppositepiece;
		if(piecevalue.equals("x")){
			oppositepiece="o";
		}
		else if(piecevalue.equals("o")){
			oppositepiece="x";
		}
		else{
			oppositepiece="-";
		}
		//R=right, L=left, T=top, B=bottom, and there are diagonals such as TL=top left.
		boolean foundR=false,foundL=false,foundT=false,foundB=false,foundTL=false,foundTR=false,foundBL=false,foundBR=false;
		int checkfurtherR=1,checkfurtherL=1,checkfurtherT=1,checkfurtherB=1,checkfurtherTL=1,checkfurtherTR=1,checkfurtherBL=1,checkfurtherBR=1;
		while(board[row][column+checkfurtherR].equals(oppositepiece)){
			checkfurtherR++;
		}
		if(board[row][column+checkfurtherR].equals(piecevalue)&&checkfurtherR>1){
			foundR=true;
		}
		while(board[row][column-checkfurtherL].equals(oppositepiece)){
			checkfurtherL++;
		}
		if(board[row][column-checkfurtherL].equals(piecevalue)&&checkfurtherL>1){
			foundL=true;
		}
		while(board[row+checkfurtherB][column].equals(oppositepiece)){
			checkfurtherB++;
		}
		if(board[row+checkfurtherB][column].equals(piecevalue)&&checkfurtherB>1){
			foundB=true;
		}
		while(board[row-checkfurtherT][column].equals(oppositepiece)){
			checkfurtherT++;
		}
		if(board[row-checkfurtherT][column].equals(piecevalue)&&checkfurtherT>1){
			foundT=true;
		}
		while(board[row-checkfurtherTR][column+checkfurtherTR].equals(oppositepiece)){
			checkfurtherTR++;
		}
		if(board[row-checkfurtherTR][column+checkfurtherTR].equals(piecevalue)&&checkfurtherTR>1){
			foundTR=true;
		}
		while(board[row-checkfurtherTL][column-checkfurtherTL].equals(oppositepiece)){
			checkfurtherTL++;
		}
		if(board[row-checkfurtherTL][column-checkfurtherTL].equals(piecevalue)&&checkfurtherTL>1){
			foundTL=true;
		}
		while(board[row+checkfurtherBL][column-checkfurtherBL].equals(oppositepiece)){
			checkfurtherBL++;
		}
		if(board[row+checkfurtherBL][column-checkfurtherBL].equals(piecevalue)&&checkfurtherBL>1){
			foundBL=true;
		}
		while(board[row+checkfurtherBR][column+checkfurtherBR].equals(oppositepiece)){
			checkfurtherBR++;
		}
		if(board[row+checkfurtherBR][column+checkfurtherBR].equals(piecevalue)&&checkfurtherBR>1){
			foundBR=true;
		}
		if(foundR==true||foundL==true||foundT==true||foundB==true||foundTL==true||foundTR==true||foundBL==true||foundBR==true){
			validation=true;
		}
		else{
			validation=false;
		}
	}
	//This method uses the same mechanics as the boardcheck or validationcheck2 methods but actually changes the board, it doesn't just check.
	public static void changepieces(int row, int column, String piecevalue){
		String oppositepiece;
		if(piecevalue.equals("x")){
			oppositepiece="o";
		}
		else if(piecevalue.equals("o")){
			oppositepiece="x";
		}
		else{
			oppositepiece="-";
		}
		//R=right, L=left, T=top, B=bottom, and there are diagonals such as TL=top left.
		boolean foundR=false,foundL=false,foundT=false,foundB=false,foundTL=false,foundTR=false,foundBL=false,foundBR=false;
		int checkfurtherR=1,checkfurtherL=1,checkfurtherT=1,checkfurtherB=1,checkfurtherTL=1,checkfurtherTR=1,checkfurtherBL=1,checkfurtherBR=1;
		while(board[row][column+checkfurtherR].equals(oppositepiece)){
			checkfurtherR++;
		}
		if(board[row][column+checkfurtherR].equals(piecevalue)){
			foundR=true;
		}
		//The board changes the board if a 'connection' has been found in the [right] direction. It makes the connection following the game rules.
		if(foundR==true){
		for(int i=column;i<column+checkfurtherR;i++){
				board[row][i]=(piecevalue);
			}
		}
		while(board[row][column-checkfurtherL].equals(oppositepiece)){
			checkfurtherL++;
		}
		if(board[row][column-checkfurtherL].equals(piecevalue)){
			foundL=true;
		}
		//Again, if something is found in the [left] direction, this block of code will be initialized to change to board array (making that 'connection' on the board).
		if(foundL==true){
		for(int i=column;i>column-checkfurtherL;i--){
				board[row][i]=(piecevalue);
			}
		}
		while(board[row+checkfurtherB][column].equals(oppositepiece)){
			checkfurtherB++;
		}
		if(board[row+checkfurtherB][column].equals(piecevalue)){
			foundB=true;
		}
		if(foundB==true){
		for(int i=row;i<row+checkfurtherB;i++){
				board[i][column]=(piecevalue);
			}
		}
		while(board[row-checkfurtherT][column].equals(oppositepiece)){
			checkfurtherT++;
		}
		if(board[row-checkfurtherT][column].equals(piecevalue)){
			foundT=true;
		}
		if(foundT==true){
		for(int i=row;i>row-checkfurtherT;i--){
				board[i][column]=(piecevalue);
			}
		}
		//Diagonal directions are harder and different from the 4 basic directions
		//It must use dynamic board to 'mark' the coordinates that it must convert to make a proper 'connection'
		while(board[row-checkfurtherTR][column+checkfurtherTR].equals(oppositepiece)){
			//each coordinate that is reached will be recorded on the dynamic board
			dynamicboard[row-checkfurtherTR][column+checkfurtherTR]=1;
			checkfurtherTR++;
		}
		if(board[row-checkfurtherTR][column+checkfurtherTR].equals(piecevalue)){
			foundTR=true;
		}
		//Now the board will be changed if an available move has been found
		if(foundTR==true){
			for(int x=row;x>row-checkfurtherTR;x--){
				for(int i=column;i<column+checkfurtherTR;i++){
					//without the use of the dynamic board, some units of the board that should not be converted, will be converted.
					//(hard to explain)Example, if coordinate places a piece on the Top right of an available move, the connection and change on the board will be performed but [down] and [right] directions will also be inconveniently converted 
					if(dynamicboard[x][i]==1){
						board[x][i]=(piecevalue);
					}
				}
			}
		}
		//dynamicboard must be cleared each time for its next job in 'marking' units in the array to be converted.
		cleandynamicboard();
		while(board[row-checkfurtherTL][column-checkfurtherTL].equals(oppositepiece)){
			dynamicboard[row-checkfurtherTL][column-checkfurtherTL]=1;
			checkfurtherTL++;
		}
		if(board[row-checkfurtherTL][column-checkfurtherTL].equals(piecevalue)){
			foundTL=true;
		}
		if(foundTL==true){
			for(int x=row;x>row-checkfurtherTL;x--){
				for(int i=column;i>column-checkfurtherTL;i--){
					if(dynamicboard[x][i]==1){
						board[x][i]=(piecevalue);
					}
				}
			}
		}
		cleandynamicboard();
		while(board[row+checkfurtherBL][column-checkfurtherBL].equals(oppositepiece)){
			dynamicboard[row+checkfurtherBL][column-checkfurtherBL]=1;
			checkfurtherBL++;
		}
		if(board[row+checkfurtherBL][column-checkfurtherBL].equals(piecevalue)){
			foundBL=true;
		}
		if(foundBL==true){
			for(int x=row;x<row+checkfurtherBL;x++){
				for(int i=column;i>column-checkfurtherBL;i--){
					if(dynamicboard[x][i]==1){
						board[x][i]=(piecevalue);
					}
				}
			}
		}
		cleandynamicboard();
		while(board[row+checkfurtherBR][column+checkfurtherBR].equals(oppositepiece)){
			dynamicboard[row+checkfurtherBR][column+checkfurtherBR]=1;
			checkfurtherBR++;
		}
		if(board[row+checkfurtherBR][column+checkfurtherBR].equals(piecevalue)){
			foundBR=true;
		}
		if(foundBR==true){
			for(int x=row;x<row+checkfurtherBR;x++){
				for(int i=column;i<column+checkfurtherBR;i++){
					if(dynamicboard[x][i]==1){
						board[x][i]=(piecevalue);
					}
				}
			}
		}
	}
	//This method simply goes through the whole board the check player points and set their score for the main method.
	public static void playerpoints(String board[][]){
		int player1pointcount=0, player2pointcount=0;
		for(int x=1;x<=boardwidth;x++){
			for(int i=1;i<=boardlength;i++){
				if(board[x][i]=="x"){
					player1pointcount++;
				}
				if(board[x][i]=="o"){
					player2pointcount++;
				}
			}
		}
		player1score=player1pointcount;
		player2score=player2pointcount;
	}
	//cleans dynamicboard for the changepieces method.
	public static void cleandynamicboard(){
		for(int x=0;x<boardwidth+2;x++){
			for(int i=0;i<boardlength+2;i++){
				dynamicboard[x][i]=0;
			}
		}
	}
}

