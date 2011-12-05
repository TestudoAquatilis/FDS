package scheduling;

import java.util.*;

public class RessourceGraph
{
	protected TreeSet <Ressource> m_ressources;
	protected TreeSet <Operation> m_operations;

	protected TreeMap <Operation, Ressource> m_edges;
	protected TreeMap <Ressource, TreeSet <Operation>> m_predecessors;

	public RessourceGraph ()
	{
		m_ressources = new TreeSet <Ressource> ();
		m_operations = new TreeSet <Operation> ();

		m_edges        = new TreeMap <Operation, Ressource> ();
		m_predecessors = new TreeMap <Ressource, TreeSet <Operation>> ();
	}

	public boolean addEdge (Operation operation, Ressource ressource)
	{
		if (m_operations.contains (operation)) return false;

		m_operations.add (operation);

		m_edges.put (operation, ressource);

		TreeSet <Operation> predecessor_set;

		if (m_ressources.contains (ressource)) {
			predecessor_set = m_predecessors.get (ressource);
		} else {
			predecessor_set = new TreeSet <Operation> ();
			m_predecessors.put (ressource, predecessor_set);
			m_ressources.add (ressource);
		}

		predecessor_set.add (operation);

		return true;
	}

	public double getRessourceUsage (Ressource ressource, int timestep)
	{
		if (! m_ressources.contains (ressource)) return 0;

		TreeSet <Operation> operations = m_predecessors.get (ressource);

		double result = 0;

		for (Operation i_op : operations) {
			result += i_op.getProbability (timestep);
		}
		
		return result;
	}

	public double getSelfForce (Operation operation, int time)
	{
		if (! m_operations.contains (operation)) return 0;

		Ressource ressource = m_edges.get (operation);

		TreeSet <Operation> other_operations = new TreeSet <Operation> (m_predecessors.get (ressource));
		other_operations.remove (operation);

		// operation assumed to be scheduled at time
		double ressource_usage_at_time = 1; 

		// ressource usage at time
		for (Operation i_op : other_operations) {
			ressource_usage_at_time += i_op.getProbability (time);
		}

		// overall ressource usage during mobility of operation
		double overall_ressource_usage = ressource_usage_at_time;

		for (int i_time = operation.getStartSoonest (); i_time <= operation.getStartLatest (); i_time ++) {
			// time already calculated ...
			if (i_time == time) continue;

			for (Operation i_op : other_operations) {
				overall_ressource_usage += i_op.getProbability (i_time);
			}
		}

		double average_ressource_usage = overall_ressource_usage / ((double) (operation.getMobility () + 1));

		return ressource_usage_at_time - average_ressource_usage;
	}

	public Set <Ressource> getRessources ()
	{
		TreeSet <Ressource> result = new TreeSet <Ressource> (m_ressources);
		//Object result = m_ressources.clone ();

		return (TreeSet <Ressource>) result;
	}
}
