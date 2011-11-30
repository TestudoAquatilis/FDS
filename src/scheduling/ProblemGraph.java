package scheduling;

import java.util.*;

public class ProblemGraph
{
	protected TreeSet <Operation> m_operations;

	protected TreeMap <Operation, TreeSet <Operation>> m_successor_map;
	protected TreeMap <Operation, TreeSet <Operation>> m_predecessor_map;

	public ProblemGraph ()
	{
		m_operations      = new TreeSet <Operation> ();
		m_successor_map   = new TreeMap <Operation, TreeSet <Operation>> ();
		m_predecessor_map = new TreeMap <Operation, TreeSet <Operation>> ();
	}

	public void addVertex (Operation operation)
	{
		m_operations.add      (operation);
		m_predecessor_map.put (operation, new TreeSet <Operation> ());
		m_successor_map.put   (operation, new TreeSet <Operation> ());
	}

	public boolean addEdge (Operation predecessor, Operation successor)
	{
		if (predecessor.equals (successor)) return false;

		// add successor as successor of predecessor
		TreeSet <Operation> successor_set;

		if (m_operations.contains (predecessor)) {
			successor_set = m_successor_map.get (predecessor);
		} else {
			m_operations.add (predecessor);
			successor_set = new TreeSet <Operation> ();

			m_successor_map.put   (predecessor, successor_set);
			m_predecessor_map.put (predecessor, new TreeSet <Operation> ());
		}

		successor_set.add (successor);

		// add predecessor as predecessor of successor
		TreeSet <Operation> predecessor_set;

		if (m_operations.contains (successor)) {
			predecessor_set = m_predecessor_map.get (successor);
		} else {
			m_operations.add (successor);
			predecessor_set = new TreeSet <Operation> ();

			m_predecessor_map.put (successor, predecessor_set);
			m_successor_map.put   (successor, new TreeSet <Operation> ());
		}

		predecessor_set.add (predecessor);

		return true;
	}
}
