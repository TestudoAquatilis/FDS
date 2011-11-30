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
}
