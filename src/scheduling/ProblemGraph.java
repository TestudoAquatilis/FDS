package scheduling;

import java.util.*;

public class ProblemGraph
{
	protected TreeSet <Operation> m_operations;

	protected int m_l_max;

	public TreeSet<Operation> operations() {
		return m_operations;
	}

	protected TreeMap <Operation, TreeSet <Operation>> m_successor_map;
	protected TreeMap <Operation, TreeSet <Operation>> m_predecessor_map;

	public ProblemGraph ()
	{
		m_operations      = new TreeSet <Operation> ();
		m_successor_map   = new TreeMap <Operation, TreeSet <Operation>> ();
		m_predecessor_map = new TreeMap <Operation, TreeSet <Operation>> ();
	}
	
	public TreeMap<Operation, TreeSet<Operation>> predecessor_map() {
		return m_predecessor_map;
	}
	public TreeMap<Operation, TreeSet<Operation>> successor_map() {
		return m_successor_map;
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

	public int getLMax ()
	{
		return m_l_max;
	}

	public TreeSet <Operation> getPlannableOperations ()
	{
		TreeSet <Operation> result = new TreeSet <Operation> ();

		for (Operation i_op : m_operations) {
			if (i_op.getFixed ()) continue;
			if (i_op.getMobility () <= 0) continue;

			result.add (i_op);
		}

		return result;
	}

	public TreeSet <Operation> getPredecessors (Operation operation)
	{
		if (! m_operations.contains (operation)) return null;

		return new TreeSet <Operation> (m_predecessor_map.get (operation));
	}
	
	public TreeSet <Operation> getSuccessors (Operation operation)
	{
		if (! m_operations.contains (operation)) return null;

		return new TreeSet <Operation> (m_successor_map.get (operation));
	}
	
	//calculate soonest and latest start for every operation via ALAP and ASAP
	public boolean calculateMobility(int Lmax){
		TreeSet <Operation> unplannedOperations;
		boolean allPredPlanned;
		boolean allSuccPlanned;
		
		unplannedOperations = new TreeSet <Operation> ();
		unplannedOperations.addAll(m_operations);
		int test_Lmax=1;
		
		//remove all via FDS fixed operations from 'unplanned operations'
		for (Iterator<Operation> iter = unplannedOperations.iterator(); iter.hasNext();){
			Operation op = iter.next();
				if(op.getFixed() == true){
					iter.remove();
				}
		}
		
		//ASAP
		//set all soonestStarts to 0 for operations without predecessor
		for (Iterator<Operation> iter = unplannedOperations.iterator(); iter.hasNext();) {
			Operation selectedOperation = iter.next();
			//FOREACH (vi without predecessor)
			if(m_predecessor_map.get(selectedOperation).isEmpty()){		
				//tau(vi):=0; (initial=0)
				selectedOperation.setStartLatest(0);
				iter.remove();								
			}
		}
		//calculate soonestStarts of all remaining operations
		while(!unplannedOperations.isEmpty()){
			for (Iterator<Operation> iter = unplannedOperations.iterator(); iter.hasNext();) {
				Operation selectedOperation = iter.next();
				//assumption: all predecessors planned
				allPredPlanned = true;															
				
				//test if all predecessors planned
				
				for (Operation selectedPredOperation : m_predecessor_map.get(selectedOperation)) {
					if(unplannedOperations.contains(selectedPredOperation)){
						allPredPlanned = false;
					}
				}
				//tau(vi) = max(vj + dj)
				if(allPredPlanned){
					selectedOperation.setStartSoonest(calcSoonestPossibleStart(selectedOperation));
						//successive calculation of entire latency test_Lmax (needed for ALAP)
						if(selectedOperation.getEndSoonest() > test_Lmax){
							test_Lmax = selectedOperation.getEndSoonest();
						}
					iter.remove();
				}
				
			}
		}
		
		//test if time constraint is long enough to execute ALAP
		if(Lmax < test_Lmax){
			System.err.println("Time constraint too short to solve scheduling problem. Please increase!");
			System.exit(0);
		}
		
		//ALAP
		unplannedOperations = new TreeSet <Operation> ();
		unplannedOperations.addAll (m_operations);
		
		//remove all via FDS fixed operations from 'unplanned operations'
		for (Iterator<Operation> iter = unplannedOperations.iterator(); iter.hasNext();){
			Operation op = iter.next();
				if(op.getFixed() == true){
					iter.remove();
				}
		}
		
		//set all latestStarts to Lmax-di for operations without successor
		for (Iterator<Operation> iter = unplannedOperations.iterator(); iter.hasNext();) {
			Operation selectedOperation = iter.next ();
			//FOREACH (vi without successor)
			if(m_successor_map.get(selectedOperation).isEmpty()){					
				//tau(vi):=Lmax-di;
				selectedOperation.setStartLatest(Lmax-selectedOperation.getLatency());
				iter.remove ();
			}
		}
		//calculate latestStarts of all remaining operations
		while(!unplannedOperations.isEmpty()){
			for (Iterator<Operation> iter = unplannedOperations.iterator(); iter.hasNext();) {
				Operation selectedOperation = iter.next ();
				//assumption: all successors planned
				allSuccPlanned = true;															

				//test if all successors planned
				for (Operation selectedSuccOperation : m_successor_map.get(selectedOperation)) {
					if(unplannedOperations.contains(selectedSuccOperation)){
						allSuccPlanned = false;
					}
				}
				//tau(vi) = min(vj) - di
				if(allSuccPlanned){
					selectedOperation.setStartLatest(calcLatestPossibleEnd(selectedOperation)-selectedOperation.getLatency());
					iter.remove ();
				}

			}
			}

		m_l_max = Lmax;

		return true;
	}
	
	//calculates the latest possible end of operation op according to its successors
	protected int calcLatestPossibleEnd(Operation selectedOperation) {
		//set latest start of first successor in TreeSet as default
		int min_t = m_successor_map.get(selectedOperation).first().getStartLatest();
			
		for (Operation succOperation : m_successor_map.get(selectedOperation)) {
			if(succOperation.getStartLatest() < min_t){
				min_t = succOperation.getStartLatest();
			}
		}
		
		return min_t;
	}

	//calculates the soonest possible start of operation op according to its predecessors
	protected int calcSoonestPossibleStart(Operation selectedOperation) {
		int max_t=0;
		
		for (Operation predOperation : m_predecessor_map.get(selectedOperation)) {
			if(predOperation.getEndSoonest() > max_t){
				max_t = predOperation.getEndSoonest();
			}
		}
		
		return max_t;
	}

}
