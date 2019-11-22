import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

public class CS245A1 {

	private static Scanner keyboard = new Scanner(System.in);

	// Make a suggestion using the tree data structure
	private static String treeSuggest(String word, AvlTree<String> t) {
		if (t.has(word)) {
			System.out.println("Word is correctly spelled");
			return word;
		} else {
			System.out.println("Select among the following suggestions");
			ArrayList<String> suggestions = new ArrayList<>();
			int i = 0;
			String succ = word;
			String pred = word;
			do {
				succ = t.successor(succ);
				if (succ != null) {
					suggestions.add(succ);
					i++;
				}
				pred = t.predecessor(pred);
				if (pred != null) {
					suggestions.add(pred);
					i++;
				}
			} while (i < 3 && (succ != null || pred != null));
			for (int j = 0; j < suggestions.size(); j++) {
				if (j >= 3) {
					break;
				}
				System.out.println((j + 1) + ": " + suggestions.get(j));
			}
			int choice = keyboard.nextInt();
			return suggestions.get(choice - 1);
		}
	}

	// Make a suggestion using the trie data structure
	private static String trieSuggest(String word, Trie trie) {
		if (trie.contains(word)) {
			System.out.println("Word is correctly spelled");
			return word;
		} else {
			System.out.println("Select among the following suggestions");
			ArrayList<String> suggestions = new ArrayList<>();
			outer: for (int i = word.length(); i > 0; i++) {
				for (String s : trie.keysWithPrefix(word.substring(0, i))) {
					if (!s.equals(word)) {
						suggestions.add(s);
					}
					if (suggestions.size() >= 3) {
						break outer;
					}
				}
			}
			for (int j = 0; j < suggestions.size(); j++) {
				if (j >= 3) {
					break;
				}
				System.out.println((j + 1) + ": " + suggestions.get(j));
			}
			int choice = keyboard.nextInt();
			return suggestions.get(choice - 1);
		}
	}

	// read a line from input, make a correction if it is wrong
	// and output the result to the output file
	private static void processData(Scanner sc, PrintWriter pw, boolean useTree, AvlTree<String> tree, Trie trie) {
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (!line.isBlank()) {
				if (useTree) {
					line = treeSuggest(line, tree);
				} else {
					line = trieSuggest(line, trie);
				}
			}
			pw.println(line);
		}
	}

	// Input to the program is the input file name
	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(new File("english.0"));
			BufferedReader br = new BufferedReader(new FileReader("conf.txt"));
			PrintWriter pw = new PrintWriter(new FileWriter("output.txt"));
			Properties p = new Properties();
			p.load(br);
			boolean tree = false;

			for (Entry<Object, Object> e : p.entrySet()) {
				Object type = e.getValue();
				tree = type.equals("tree");
				break;
			}
			if (tree) {
				AvlTree<String> t = new AvlTree<>();
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.isBlank()) {
						t.insert(line);
					}
				}
				sc.close();

				sc = new Scanner(new File(args[0]));
				processData(sc, pw, tree, t, null);
			} else {
				Trie trie = new Trie();
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.isBlank()) {
						trie.add(line);
					}
				}

				sc.close();

				sc = new Scanner(new File(args[0]));
				processData(sc, pw, tree, null, trie);
			}
			sc.close();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
