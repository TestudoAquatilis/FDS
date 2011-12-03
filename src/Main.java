import scheduling.*;

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

			if (! success) System.err.println ("Error reading from file " + filename);
		} else {
			System.err.println ("Error: no input file specified!");
		}
		
		//test mobility calculation
		System.out.println("--------------------------------");
		System.out.println("overview operations (ASAP, ALAP)");
		System.out.println("--------------------------------");
		m_scheduling_problem.problemGraph().calculateMobility();
		for (Operation op : m_scheduling_problem.problemGraph().operations()){
			System.out.println( "ID: " + op.getId() + " | NumOfPred: " + m_scheduling_problem.problemGraph().predecessor_map().get(op).size()+ " | NumOfSucc: " + m_scheduling_problem.problemGraph().successor_map().get(op).size() + " | soonest start: " + op.getStartSoonest() + " | latest start: " + op.getStartLatest() + " | mobility: " + op.getMobility());
		}
	}
}
