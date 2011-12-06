import scheduling.*;
import java.util.*;

public class Main
{
	protected static SchedulingProblem m_scheduling_problem;

	public static void main (String[] args)
	{
		m_scheduling_problem = new SchedulingProblem ();

		boolean success = false;

		if (args.length > 0) {
			String filename = args[0];

			success = m_scheduling_problem.readFromFile (filename);

			if (! success){ System.err.println ("Error reading from file " + filename); System.exit(0);}
		} else {
			System.err.println ("Error: no input file specified!");
			System.exit(0);
		}
		
		//test mobility calculation
		System.err.println("--------------------------------");
		System.err.println("overview operations (ASAP, ALAP)");
		System.err.println("--------------------------------");
		m_scheduling_problem.problemGraph().calculateMobility(m_scheduling_problem.getTimingConstraint());
		for (Operation op : m_scheduling_problem.problemGraph().operations()){
			System.err.println( "ID: " + op.getId() + " | NumOfPred: " + m_scheduling_problem.problemGraph().predecessor_map().get(op).size()+ " | NumOfSucc: " + m_scheduling_problem.problemGraph().successor_map().get(op).size() + " | soonest start: " + op.getStartSoonest() + " | latest start: " + op.getStartLatest() + " | mobility: " + op.getMobility());
		}

		//test probabilities
		/*
		System.err.println("--------------------------------");
		System.err.println("FDS probabilities ..............");
		System.err.println("--------------------------------");

		Set <Operation> operations = m_scheduling_problem.operations ();

		for (int i_time = 0; i_time < m_scheduling_problem.problemGraph ().getLMax (); i_time ++) {
			for (Operation i_op : operations) {
				System.err.println ("  Time " + i_time + ": Operation " + i_op.getId () + ": Probability = " + i_op.getProbability (i_time));
			}
		}
		*/

		System.err.println("--------------------------------");
		System.err.println("FDS ressource usages ...........");
		System.err.println("--------------------------------");
		
		Set <Ressource> ressources = m_scheduling_problem.ressources ();

		for (int i_time = 0; i_time < m_scheduling_problem.problemGraph ().getLMax (); i_time ++) {
			String current_line = "Timestep " + i_time + ":";

			for (Ressource i_res : ressources) {
				current_line += " Ressource " + i_res.getId () + ": ";
				current_line += m_scheduling_problem.ressourceGraph ().getRessourceUsage (i_res, i_time) + ";";
			}

			System.err.println (current_line);
		}

		System.out.println("--------------------------------");
		System.out.println("FDS Scheduling result ..........");
		System.out.println("--------------------------------");
		
		// calculate scheduling ... if this works we get a cake
		m_scheduling_problem.calculateFDSScheduling ();

		// test result:
		Set <Operation> operations = m_scheduling_problem.operations ();

		for (Operation i_operation : operations) {
			System.out.println ("Operation " + i_operation.getId () + ": start at " + i_operation.getStartSoonest ());
		}

		// nicer print:
		System.out.println("--------------------------------");
		System.out.println("FDS Scheduling result ..........");
		System.out.println("--------------------------------");
		
		TreeMap <Integer, TreeSet <Operation>> fds_scheduling = new TreeMap <Integer, TreeSet <Operation>> ();
		for (int i_time = 0; i_time < m_scheduling_problem.getTimingConstraint (); i_time ++) {
			fds_scheduling.put (i_time, new TreeSet <Operation> ());
		}

		for (Operation i_op : operations) {
			TreeSet <Operation> scheduling_at_time = fds_scheduling.get (i_op.getStartSoonest ());

			scheduling_at_time.add (i_op);
		}

		for (int i_time = 0; i_time < m_scheduling_problem.getTimingConstraint (); i_time ++) {
			TreeSet <Operation> scheduling_at_time = fds_scheduling.get (i_time);

			String current_line = "Time " + i_time + ":";

			for (Operation i_op : scheduling_at_time) {
				current_line += " " + i_op.getId ();
			}

			current_line += "  ---  Ressource usage: ";

			for (Ressource i_res : ressources) {
				current_line += " r" + i_res.getId () + ": ";
				current_line += m_scheduling_problem.ressourceGraph ().getRessourceUsage (i_res, i_time) + ";";
			}

			System.out.println (current_line);
		}
	}
}
