package scheduling;

import java.util.*;

public class Operation
	implements Comparable <Operation>
{
	protected int m_id;
	protected int m_latency;

	protected boolean m_fixed;

	protected int m_start_soonest;
	protected int m_start_latest;

	public Operation (int operation_id, int latency)
	{
		m_id       = operation_id;
		m_latency  = latency;

		m_fixed    = false;

		m_start_soonest = 0;
		m_start_latest  = Integer.MAX_VALUE;
	}

	public int getId ()
	{
		return m_id;
	}

	public boolean getFixed ()
	{
		return m_fixed;
	}

	public void setFixed ()
	{
		m_fixed = true;
	}

	public void setNotFixed ()
	{
		m_fixed = false;
	}
	
	public int getLatency ()
	{
		return m_latency;
	}

	public int getStartSoonest ()
	{
		return m_start_soonest;
	}
	
	public void setStartSoonest (int startSoonest)
	{
		m_start_soonest = startSoonest;
	}

	public int getStartLatest ()
	{
		return m_start_latest;
	}

	public void setStartLatest (int startLatest)
	{
		m_start_latest = startLatest;
	}
	
	public int getEndSoonest ()
	{
		return m_start_soonest + m_latency;
	}

	public int getEndLatest ()
	{
		return m_start_latest + m_latency;
	}

	public int getMobility ()
	{
		return m_start_latest - m_start_soonest;
	}

	public double getProbability (int Timestep)
	{
		if (Timestep > m_start_latest) return 0;
		if (Timestep < m_start_soonest) return 0;

		double result = 1.0 / (double) (getMobility () + 1);

		return result;
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

		if (operation.m_id > m_id) return -1;

		return 0;
	}
}
