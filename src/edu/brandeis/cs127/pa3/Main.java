package edu.brandeis.cs127.pa3;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
   the main class 
   @author cs127
 */
public class Main{

	static BufferedReader in;




	public static BTree buildTree(String[] commands) {
		BTree T = new BTree (3);
		int n;
		for (String s : commands) {
			char c = s.charAt(0);

			switch (c){
			case 'n':
				n = Integer.valueOf(s.substring(1));
				if (n < 3){
					System.out.println ("no tree created: degree has to be greater than 3");
				} else { 
					T = new BTree (n);
					System.out.println("B+ Tree of degree "+n+" created");
				}
				break;
			case 'o':
				T.outputForGraphviz();
				break;
			case 'l':
				n = Integer.valueOf(s.substring(1));
				T.search (n);
				break;
			case 'i':
				n = Integer.valueOf(s.substring(1));
				T.insert (n);
				break;
			case 'd':
				n = Integer.valueOf(s.substring(1));
				T.delete (n);
				break;
			case 'p':
				T.print ();
				break;
			default:
				break;
			}
		}
		
		return T;

	}

	/**
       the routine that accept command and execute it
	 */
	public static void main (String[] args){
		System.out.println ("commands: nX (new tree degree X), l (locate), i (insert), d (delete), q (quit)");

		Scanner sc = new Scanner(System.in);
		
		List<String> commands = new ArrayList<String>();
		
		String line;
		while (!(line = sc.nextLine()).equals("q")) {
			commands.add(line);
		}
		
		buildTree(commands.toArray(new String[] {}));
		
		sc.close();
	}
}
