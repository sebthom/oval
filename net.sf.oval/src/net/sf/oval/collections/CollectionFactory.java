package net.sf.oval.collections;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public abstract class CollectionFactory
{
	private final static Logger LOG = Logger.getLogger(CollectionFactory.class.getName());

	public static CollectionFactory INSTANCE;

	static
	{
		// if javalution collection classes are found use them by default
		try
		{
			Class.forName("javolution.util.FastMap");
			Class.forName("javolution.util.FastSet");
			Class.forName("javolution.util.FastTable");

			INSTANCE = CollectionFactoryJavalutionImpl.INSTANCE;

			LOG.info("javolution.util collection classes are available.");
		}
		catch (ClassNotFoundException e)
		{
			INSTANCE = CollectionFactoryJDKImpl.INSTANCE;
		}
	}

	/**
	 * Instantiate an ArrayList like list object
	 */
	public abstract <ValueType> List<ValueType> createList();

	/**
	 * Instantiate an ArrayList like list object
	 */
	public abstract <ValueType> List<ValueType> createList(int initialCapacity);

	/**
	 * Instantiate a HashMap like map object
	 */
	public abstract <KeyType, ValueType> Map<KeyType, ValueType> createMap();

	/**
	 * Instantiate a HashMap like map object
	 */
	public abstract <KeyType, ValueType> Map<KeyType, ValueType> createMap(int initialCapacity);

	/**
	 * Instantiate a HashSet like set object
	 */
	public abstract <ValueType> Set<ValueType> createSet();

	/**
	 * Instantiate a HashSet like set object
	 */
	public abstract <ValueType> Set<ValueType> createSet(int initialCapacity);
}
