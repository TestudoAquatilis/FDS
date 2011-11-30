package scheduling;

import java.util.*;

public class Operation
	implements Comparable <Operation>
{
	protected int m_id;
	protected int m_latency;

	protected int m_start_soonest;
	protected int m_start_latest;

	public Operation (int operation_id, int latency)
	{
		m_id       = operation_id;
		m_latency = latency;

		m_start_soonest = 0;
		m_start_latest  = 0;
	}

	public int id ()
	{
		return m_id;
	}

	public int startSoonest ()
	{
		return m_start_soonest;
	}
	
	public void startSoonestSet (int m_start_soonest)
	{
		this.m_start_soonest = m_start_soonest;
	}

	public int startLatest ()
	{
		return m_start_latest;
	}

	public void startLatestSet (int m_start_latest)
	{
		this.m_start_latest=m_start_latest;
	}
	
	public int endSoonest ()
	{
		return m_start_soonest + m_latency;
	}

	public int endLatest ()
	{
		return m_start_latest + m_latency;
	}

	public int mobility ()
	{
		return m_start_latest - m_start_soonest;
	}

	public int hashCode ()
	{
		return m_id;
	}

	public boolean equals (Object o)
	{
		if (o == null) {
			if (this == null) return true;
			return false;
		}

		if (!(o instanceof Operation)) return false;

		Operation operation = (Operation) o;

		if (operation.m_id != m_id) return false;

		return true;
	}

	public int compareTo (Operation operation) throws NullPointerException
	{
		if (operation == null) throw new NullPointerException ();

		if (operation.m_id < m_id) return 1;

		if (operation.m_id > m_id) return 0;

		return 0;
	}
}
