package net.sf.oval.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionFactoryJDKImpl extends CollectionFactory
{
	public final static CollectionFactoryJDKImpl INSTANCE = new CollectionFactoryJDKImpl();

	protected CollectionFactoryJDKImpl()
	{}

	public <ValueType> List<ValueType> createList()
	{
		return new ArrayList<ValueType>();
	}

	public <ValueType> List<ValueType> createList(final int initialCapacity)
	{
		return new ArrayList<ValueType>(initialCapacity);
	}

	public <KeyType, ValueType> Map<KeyType, ValueType> createMap()
	{
		return new HashMap<KeyType, ValueType>();
	}

	public <KeyType, ValueType> Map<KeyType, ValueType> createMap(final int initialCapacity)
	{
		return new HashMap<KeyType, ValueType>(initialCapacity);
	}

	public <ValueType> Set<ValueType> createSet()
	{
		return new HashSet<ValueType>();
	}

	public <ValueType> Set<ValueType> createSet(final int initialCapacity)
	{
		return new HashSet<ValueType>(initialCapacity);
	}
}
