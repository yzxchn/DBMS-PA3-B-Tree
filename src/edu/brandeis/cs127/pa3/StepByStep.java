package edu.brandeis.cs127.pa3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class StepByStep {

	public static void main (String[] args){
		System.out.println ("commands: nX (new tree degree X), l (locate), i (insert), d (delete), q (quit)");

		Scanner sc = new Scanner(System.in);
		
		List<String> commands = new ArrayList<String>();
		
		String line;
		while (!(line = sc.nextLine()).equals("q")) {
			commands.add(line);
		}
		
		LinkedList<String> currentCommands = new LinkedList<String>();
		int commandPtr = 0;
		while (true){
			System.out.println("f to advance one step, b to go back one step, q to quit");
			
			String l = sc.nextLine();
			if (l.equals("q")){
				break;
			}
			if (l.equals("f")){
				currentCommands.add(commands.get(commandPtr));
				commandPtr++;
			} else if (l.equals("b")){
				currentCommands.removeLast();
				commandPtr--;
			}
			
			System.out.println(currentCommands.getLast());
			BTree T = Main.buildTree(currentCommands.toArray(new String[] {}));
			System.out.println("Next Command: "+commands.get(commandPtr));
			T.print();
		}	
		sc.close();
	}
}
