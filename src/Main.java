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
	}
}
