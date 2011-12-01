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
	
	//calculate soonest and latest start for every operation via ALAP and ASAP
	public boolean calculateMobility(){
		TreeSet <Operation> unplannedOperations;
		
		unplannedOperations = new TreeSet <Operation> ();
		unplannedOperations.addAll (m_operations);
		int Lmax=1;
		
		//ASAP
		//set all soonestStarts to 0 for operations without predecessor
		for (Operation selectedOperation : unplannedOperations) {
			if(m_predecessor_map.get(selectedOperation).isEmpty()){							//FOREACH (vi without predecessor)
				unplannedOperations.remove(selectedOperation);								//tau(vi):=0; (initial=0)
			}
		}
		
		//ALAP
		unplannedOperations = new TreeSet <Operation> ();
		unplannedOperations.addAll (m_operations);
		//set all latestStarts to Lmax-di for operations without successor
		for (Operation selectedOperation : unplannedOperations) {
			if(m_successor_map.get(selectedOperation).isEmpty()){							//FOREACH (vi without successor)
				selectedOperation.setStartLatest(Lmax-selectedOperation.getLatency());		//tau(vi):=Lmax-di;
				unplannedOperations.remove(selectedOperation);
			}
		}
		return true;
	}

}
