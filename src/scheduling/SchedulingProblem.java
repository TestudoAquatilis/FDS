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

	public void calculateFDSScheduling ()
	{
		while (true) {
			m_problem_graph.calculateMobility (m_timing_constraint); 
			m_ressource_graph.calculateAverageRessourceUsages ();

			TreeSet <Operation> plannable_operations = m_problem_graph.getPlannableOperations ();

			if (plannable_operations.size () <= 0) break;

			double    min_force     = Double.MAX_VALUE;
			Operation min_operation = null;
			int       min_time      = -1;

			for (Operation i_plannable_operation : plannable_operations) {
				for (int i_time = i_plannable_operation.getStartSoonest (); i_time <= i_plannable_operation.getStartLatest (); i_time ++) {
					double self_force = m_ressource_graph.getSelfForce (i_plannable_operation, i_time);
					
					double sum_predecessor_forces = 0;
					TreeSet <Operation> predecessors = m_problem_graph.getPredecessors (i_plannable_operation);

					for (Operation i_predecessor : predecessors) {
						sum_predecessor_forces += m_ressource_graph.getPredecessorForce (i_predecessor, i_time);
					}

					double sum_successor_forces = 0;
					TreeSet <Operation> successors = m_problem_graph.getSuccessors (i_plannable_operation);

					for (Operation i_successor : successors) {
						sum_successor_forces += m_ressource_graph.getSuccessorForce (i_successor, i_time);
					}

					double current_force = self_force + sum_predecessor_forces + sum_successor_forces;

					if (current_force < min_force) {
						min_force     = current_force;
						min_time      = i_time;
						min_operation = i_plannable_operation;
					}
				}
			}

			if (min_time < 0) {
				System.err.println ("FDS-Scheduling - no plannable operation - This should never happen!!!");
				System.exit (1);
			}

			min_operation.setStartSoonest (min_time);
			min_operation.setStartLatest  (min_time);
			min_operation.setFixed ();
		}
	}
	
	public int getTimingConstraint() {
		return m_timing_constraint;
	}

	public String toString () {
		StringBuffer result = new StringBuffer ();

		TreeMap <Integer, TreeSet <Operation>> scheduling = new TreeMap <Integer, TreeSet <Operation>> ();
		for (int i_time = 0; i_time < m_timing_constraint; i_time ++) {
			scheduling.put (i_time, new TreeSet <Operation> ());
		}

		for (Operation i_op : m_operations.values ()) {
			for (int i_time = i_op.getStartSoonest (); i_time < i_op.getEndLatest (); i_time ++) {
				TreeSet <Operation> scheduling_at_time = scheduling.get (i_time);

				scheduling_at_time.add (i_op);
			}
		}

		result.append ("Time | Operations   | Ressource usage\n");
		result.append ("-------------------------------------------------------\n");

		for (int i_time = 0; i_time < m_timing_constraint; i_time ++) {
			TreeSet <Operation> scheduling_at_time = scheduling.get (i_time);

			StringBuffer current_line = new StringBuffer ();

			current_line.append (i_time);
			while (current_line.length () < 5) current_line.append (" ");
			current_line.append ("|");

			for (Operation i_op : scheduling_at_time) {
				current_line.append (" ");
				current_line.append (i_op.getId ());
			}

			while (current_line.length () < 20) current_line.append (" ");
			current_line.append ("|");

			for (Ressource i_res : m_ressources.values ()) {
				current_line.append (" r" + i_res.getId () + ": ");
				current_line.append (m_ressource_graph.getRessourceUsage (i_res, i_time) + ";");
			}

			current_line.append ('\n');

			result.append (current_line);
		}

		return result.toString ();
	}
}
