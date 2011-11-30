package scheduling;

import java.util.*;

public class Ressource
	implements Comparable <Ressource>
{
	protected int m_id;

	public Ressource (int ressource_id)
	{
		m_id = ressource_id;
	}

	public int id ()
	{
		return m_id;
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

		if (!(o instanceof Ressource)) return false;

		Ressource ressource = (Ressource) o;

		if (ressource.m_id != m_id) return false;

		return true;
	}

	public int compareTo (Ressource ressource) throws NullPointerException
	{
		if (ressource == null) throw new NullPointerException ();

		if (ressource.m_id < m_id) return 1;

		if (ressource.m_id > m_id) return 0;

		return 0;
	}

}
