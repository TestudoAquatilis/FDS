package scheduling;

import java.util.*;

public class RessourceGraph
{
	protected TreeSet <Ressource> m_ressources;
	protected TreeSet <Operation> m_operations;

	protected TreeMap <Operation, Ressource> m_edges;
	protected TreeMap <Ressource, TreeSet <Operation>> m_predecessors;

	protected TreeMap <Operation, Double> m_average_ressource_usage;

	public RessourceGraph ()
	{
		m_ressources = new TreeSet <Ressource> ();
		m_operations = new TreeSet <Operation> ();

		m_edges        = new TreeMap <Operation, Ressource> ();
		m_predecessors = new TreeMap <Ressource, TreeSet <Operation>> ();

		m_average_ressource_usage = new TreeMap <Operation, Double> ();
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

	public void calculateAverageRessourceUsages ()
	{
		m_average_ressource_usage.clear ();

		for (Operation i_operation : m_operations) {
			int start_time = i_operation.getStartSoonest ();
			int end_time   = i_operation.getEndLatest ();

			Ressource ressource = m_edges.get (i_operation);
			double average_usage = 0;

			for (int i_time = start_time; i_time < end_time; i_time ++) {
				average_usage += getRessourceUsage (ressource, i_time);
			}

			average_usage /= ((double) (end_time - start_time));

			m_average_ressource_usage.put (i_operation, average_usage);
		}
	}

	public double getSelfForce (Operation operation, int time)
	{
		if (! m_operations.contains (operation)) return 0;

		Ressource ressource = m_edges.get (operation);

		TreeSet <Operation> other_operations = new TreeSet <Operation> (m_predecessors.get (ressource));
		other_operations.remove (operation);

		// operation assumed to be scheduled at time
		int latency = operation.getLatency ();
		double ressource_usage_at_time = latency;

		// ressource usage at time
		for (Operation i_op : other_operations) {
			for (int i_delta_time = 0; i_delta_time < latency; i_delta_time ++) {
				ressource_usage_at_time += i_op.getProbability (time);
			}
		}

		ressource_usage_at_time /= latency;

		double average_ressource_usage = m_average_ressource_usage.get (operation);

		return ressource_usage_at_time - average_ressource_usage;
	}

	public double getSuccessorForce (Operation successor, int time)
	{
		if (! m_operations.contains (successor)) return 0;

		Ressource ressource = m_edges.get (successor);

		TreeSet <Operation> operations = m_predecessors.get (ressource);

		int start_time = time + 1;
		int end_time   = successor.getEndLatest ();

		int old_start_soonest = successor.getStartSoonest ();
		successor.setStartSoonest (start_time);

		double average_usage_wrt_time = 0; 

		// ressource usage at time
		for (int i_time = start_time; i_time < end_time; i_time ++) {
			for (Operation i_op : operations) {
				average_usage_wrt_time += i_op.getProbability (i_time);
			}
		}

		average_usage_wrt_time /= ((double) (end_time - start_time));
		// TODO: check: was like
		//        average_usage_wrt_time /= ((double) (end_time + 1 - start_time));
		// before... but this seems to be wrong

		double average_ressource_usage = m_average_ressource_usage.get (successor);

		successor.setStartSoonest (old_start_soonest);
		return average_usage_wrt_time - average_ressource_usage;
	}

	public double getPredecessorForce (Operation predecessor, int time)
	{
		if (! m_operations.contains (predecessor)) return 0;

		Ressource ressource = m_edges.get (predecessor);

		TreeSet <Operation> operations = m_predecessors.get (ressource);

		int start_time = predecessor.getStartSoonest ();
		int end_time   = time;
		int latency    = predecessor.getLatency ();

		int old_start_latest = predecessor.getStartLatest ();
		predecessor.setStartLatest (end_time - latency);

		double average_usage_wrt_time = 0; 

		// ressource usage at time
		for (int i_time = start_time; i_time < end_time; i_time ++) {
			for (Operation i_op : operations) {
				average_usage_wrt_time += i_op.getProbability (i_time);
			}
		}

		average_usage_wrt_time /= ((double) (end_time - start_time));

		double average_ressource_usage = m_average_ressource_usage.get (predecessor);

		predecessor.setStartLatest (old_start_latest);
		return average_usage_wrt_time - average_ressource_usage;
	}

	public Set <Ressource> getRessources ()
	{
		TreeSet <Ressource> result = new TreeSet <Ressource> (m_ressources);
		//Object result = m_ressources.clone ();

		return (TreeSet <Ressource>) result;
	}
}
