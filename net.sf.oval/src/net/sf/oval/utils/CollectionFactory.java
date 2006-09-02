package net.sf.oval.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CollectionFactory
{
	private final static Logger LOG = Logger.getLogger(CollectionFactory.class.getName());

	private static Class fastMap;
	private static Class fastSet;
	private static Class fastTable;

	private static boolean javolutionAvailable;

	static
	{
		try
		{
			javolutionAvailable = false;
			fastSet = Class.forName("javolution.util.FastSet");
			fastMap = Class.forName("javolution.util.FastMap");
			fastTable = Class.forName("javolution.util.FastTable");
			javolutionAvailable = true;
			LOG.info("javolution.util collection classes are available.");
		}
		catch (ClassNotFoundException ex)
		{}
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> createList()
	{
		if (javolutionAvailable)
		{
			try
			{
				return (List<E>) fastTable.newInstance();
			}
			catch (Exception ex)
			{
				LOG.log(Level.WARNING, "Creating an instance of " + fastTable + " failed.", ex);
			}
		}
		return new ArrayList<E>();
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> createMap()
	{
		if (javolutionAvailable)
		{
			try
			{
				return (Map<K, V>) fastMap.newInstance();
			}
			catch (Exception ex)
			{
				LOG.log(Level.WARNING, "Creating an instance of " + fastMap + " failed.", ex);
			}
		}
		return new HashMap<K, V>();
	}

	@SuppressWarnings("unchecked")
	public static <E> Set<E> createSet()
	{
		if (javolutionAvailable)
		{
			try
			{
				return (Set<E>) fastSet.newInstance();
			}
			catch (Exception ex)
			{
				LOG.log(Level.WARNING, "Creating an instance of " + fastSet + " failed.", ex);
			}
		}
		return new HashSet<E>();
	}

	private CollectionFactory()
	{}
}
