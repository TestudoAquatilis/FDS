package scheduling;

import java.util.*;
import java.io.*;

public class SchedulingProblem
{
	protected int m_timing_constraint;

	protected TreeMap <Integer, Ressource> m_ressources;
	protected TreeMap <Integer, Operation> m_operations;

	protected RessourceGraph m_ressource_graph;
	protected ProblemGraph   m_problem_graph;

	public SchedulingProblem ()
	{
		m_timing_constraint = 0;

		m_ressources = new TreeMap <Integer, Ressource> ();
		m_operations = new TreeMap <Integer, Operation> ();

		m_ressource_graph = new RessourceGraph ();
		m_problem_graph   = new ProblemGraph ();
	}

	public boolean readFromFile (String filename)
	{
		boolean result = true;
		try {
			BufferedReader fileReader = new BufferedReader (new FileReader (filename));

			String current_line = fileReader.readLine ();

			while (current_line != null) {
				if (! parseInputStringLine (current_line)) {
					result = false;
					break;
				}

				current_line = fileReader.readLine ();
			}

			fileReader.close ();
		} catch (FileNotFoundException fnfex) {
			result = false;
		} catch (IOException ioex) {
			result = false;
		}

		return result;
	}

	protected boolean parseInputStringLine (String line)
	{
		boolean result = true;

		if (line.length () < 3) return true;

		switch (line.charAt (0)) {
			case 't': result = parseTimingString (line.substring (2));
				break;
			case 'o': result = parseOperationString (line.substring (2));
				break;
			case 'r': result = parseRessourceString (line.substring (2));
				break;
			case 'd': result = parseDependencyString (line.substring (2));
				break;
		}

		return result;
	}

	protected boolean parseTimingString (String line)
	{
		Scanner line_scanner = new Scanner (line);

		if (line_scanner.hasNextInt ()) {
			int next_int = line_scanner.nextInt ();

			m_timing_constraint = next_int;
		} else {
			return false;
		}

		return true;
	}

	protected boolean parseOperationString (String line)
	{
		Scanner line_scanner = new Scanner (line);

		int operation_id = -1;
		int latency      = -1;

		if (line_scanner.hasNextInt ()) {
			operation_id = line_scanner.nextInt ();
		} else {
			return false;
		}

		if (line_scanner.hasNextInt ()) {
			latency = line_scanner.nextInt ();
		} else {
			return false;
		}

		if ((operation_id < 0) || (latency < 1)) return false;

		Operation operation = new Operation (operation_id, latency);

		m_operations.put (operation_id, operation);
		m_problem_graph.addVertex (operation);

		return true;
	}

	protected boolean parseRessourceString (String line)
	{
		Scanner line_scanner = new Scanner (line);

		int operation_id = -1;
		int ressource_id = -1;

		if (line_scanner.hasNextInt ()) {
			operation_id = line_scanner.nextInt ();
		} else {
			return false;
		}

		line_scanner.findInLine ("->");

		if (line_scanner.hasNextInt ()) {
			ressource_id = line_scanner.nextInt ();
		} else {
			return false;
		}

		if ((operation_id < 0) || (ressource_id < 0)) return false;
		if (! m_operations.containsKey (operation_id)) return false;

		if (! m_ressources.containsKey (ressource_id)) {
			m_ressources.put (ressource_id, new Ressource (ressource_id));
		}

		Operation operation = m_operations.get (operation_id);

		Ressource ressource = m_ressources.get (ressource_id);

		m_ressource_graph.addEdge (operation, ressource);

		return true;
	}

	protected boolean parseDependencyString (String line)
	{
		Scanner line_scanner = new Scanner (line);

		int predecessor_id = -1;
		int successor_id   = -1;

		if (line_scanner.hasNextInt ()) {
			predecessor_id = line_scanner.nextInt ();
		} else {
			return false;
		}
		
		line_scanner.findInLine ("->");

		if (line_scanner.hasNextInt ()) {
			successor_id = line_scanner.nextInt ();
		} else {
			return false;
		}

		if ((predecessor_id < 0) || (successor_id < 0))  return false;
		if (! m_operations.containsKey (predecessor_id)) return false;
		if (! m_operations.containsKey (successor_id))   return false;

		Operation predecessor = m_operations.get (predecessor_id);
		Operation successor = m_operations.get (successor_id);

		m_problem_graph.addEdge (predecessor, successor);

		return true;
	}

	public SortedSet <Operation> operations ()
	{
		TreeSet <Operation> result = new TreeSet <Operation> ();

		result.addAll (m_operations.values ());

		return result;
	}

	public SortedSet <Ressource> ressources ()
	{
		TreeSet <Ressource> result = new TreeSet <Ressource> ();

		result.addAll (m_ressources.values ());

		return result;
	}

	public ProblemGraph problemGraph ()
	{
		return m_problem_graph;
	}

	public RessourceGraph ressourceGraph ()
	{
		return m_ressource_graph;
	}
}
