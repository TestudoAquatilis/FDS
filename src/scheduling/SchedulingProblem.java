package scheduling;

import java.util.*;

public class SchedulingProblem
{
	protected TreeMap <Integer, Ressource> m_ressources;
	protected TreeMap <Integer, Operation> m_operations;

	protected RessourceGraph m_ressource_graph;
	protected ProblemGraph   m_problem_graph;

	public SchedulingProblem ()
	{
		m_ressources = new TreeMap <Integer, Ressource> ();
		m_operations = new TreeMap <Integer, Operation> ();

		m_ressource_graph = new RessourceGraph ();
		m_problem_graph   = new ProblemGraph ();
	}

	public boolean readFromFile (String filename)
	{
		//TODO

		return false;
	}

	public SortedSet <Operation> operations ()
	{
		TreeSet <Operation> result = new TreeSet <Operation> ();

		result.addAll (m_operations.values ());

		return result;
	}

	public SortedSet <Ressource> ressources ()
	{
		TreeSet <Ressource> result = new TreeSet <Ressource> ();

		result.addAll (m_ressources.values ());

		return result;
	}

	public ProblemGraph problemGraph ()
	{
		return m_problem_graph;
	}

	public RessourceGraph ressourceGraph ()
	{
		return m_ressource_graph;
	}
}
