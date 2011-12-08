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

			if (! success){
				System.err.println ("Error reading from file " + filename); 
				System.exit (1);
			}
		} else {
			System.err.println ("Error: no input file specified!");
			System.exit (1);
		}


		// testMobilityCalculation ();
		// testProbabilityCalculation ();
		// testRessourceUsage ();

		m_scheduling_problem.calculateFDSScheduling ();
		printFDSSchedulingResult ();

		// TODO: uncomment after cake is chosen
		// printPreferredCake ();
	}

	protected static void printPreferredCake ()
	{
		System.out.println("-------------------------------------------------------");
		System.out.println("|        Our preferred cakes:                         |"); 
		System.out.println("|         - Apfelmuß-Gitter                           |");
		System.out.println("|         - Quark-Aprikosen                           |"); 
		System.out.println("|         - Kuchen des Hauses                         |"); 
		System.out.println("-------------------------------------------------------");
		System.out.println();

	}

	protected static void printFDSSchedulingResult ()
	{
		System.out.println("-------------------------------------------------------");
		System.out.println("|               FDS Scheduling result                 |"); 
		System.out.println("-------------------------------------------------------");
		System.out.println();

		System.out.println (m_scheduling_problem);
		System.out.println();
	}

	protected static void testMobilityCalculation ()
	{	
		System.err.println("--------------------------------");
		System.err.println("overview operations (ASAP, ALAP)");
		System.err.println("--------------------------------");

		m_scheduling_problem.problemGraph().calculateMobility(m_scheduling_problem.getTimingConstraint());

		for (Operation op : m_scheduling_problem.problemGraph().operations()){
			System.err.println( "ID: " + op.getId()
					+ " | NumOfPred: " + m_scheduling_problem.problemGraph().predecessor_map().get(op).size()
					+ " | NumOfSucc: " + m_scheduling_problem.problemGraph().successor_map().get(op).size()
					+ " | soonest start: " + op.getStartSoonest()
					+ " | latest start: " + op.getStartLatest()
					+ " | mobility: " + op.getMobility());
		}
	}

	protected static void testProbabilitiyCalculation ()
	{
		//test probabilities
		System.err.println("--------------------------------");
		System.err.println("FDS probabilities ..............");
		System.err.println("--------------------------------");

		m_scheduling_problem.problemGraph().calculateMobility(m_scheduling_problem.getTimingConstraint());

		Set <Operation> operations = m_scheduling_problem.operations ();

		for (int i_time = 0; i_time < m_scheduling_problem.problemGraph ().getLMax (); i_time ++) {
			for (Operation i_op : operations) {
				System.err.println ("  Time " + i_time + ": Operation " + i_op.getId () + ": Probability = " + i_op.getProbability (i_time));
			}
		}
	}

	protected static void testRessourceUsage ()
	{
		System.err.println("--------------------------------");
		System.err.println("FDS ressource usages ...........");
		System.err.println("--------------------------------");
		
		m_scheduling_problem.problemGraph().calculateMobility(m_scheduling_problem.getTimingConstraint());

		Set <Ressource> ressources = m_scheduling_problem.ressources ();

		for (int i_time = 0; i_time < m_scheduling_problem.problemGraph ().getLMax (); i_time ++) {
			String current_line = "Timestep " + i_time + ":";

			for (Ressource i_res : ressources) {
				current_line += " Ressource " + i_res.getId () + ": ";
				current_line += m_scheduling_problem.ressourceGraph ().getRessourceUsage (i_res, i_time) + ";";
			}

			System.err.println (current_line);
		}
	}
}
