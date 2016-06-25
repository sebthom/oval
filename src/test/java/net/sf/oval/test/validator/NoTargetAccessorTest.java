package net.sf.oval.test.validator;

import static java.util.Arrays.asList;
import static net.sf.oval.test.validator.NoTargetAccessorTest.Person.happy;
import static net.sf.oval.test.validator.NoTargetAccessorTest.Person.unhappy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.AssertNull;

/**
 * This test checks that filtering a collection with a predicate (expressed with
 * jxpath for example) and then checking the result works without exceptions.
 * 
 * @author anydoby
 *
 */
public class NoTargetAccessorTest
{

	private Validator sut;
	private HappyPopulation population;

	/**
	 * Prepares validator and other stuff.
	 */
	@Before
	public void pre()
	{
		sut = new Validator();
		population = new HappyPopulation();
	}

	/**
	 * Checks that all people of a happy population are happy
	 */
	@Test
	public void okTest()
	{
		population.add(happy(), happy(), happy());

		List<ConstraintViolation> list = sut.validate(population);

		assertTrue("There must be not unhappy people in this population", list.isEmpty());
	}

	/**
	 * Checks that empty population is happy
	 */
	@Test
	public void okTest_Empty()
	{
		List<ConstraintViolation> list = sut.validate(population);
		assertTrue("There must be not unhappy people in this population", list.isEmpty());
	}

	/**
	 * Checks that empty population is happy
	 */
	@Test
	public void okTest_One()
	{
		population.add(happy());

		List<ConstraintViolation> list = sut.validate(population);

		assertTrue("There must be not unhappy people in this population", list.isEmpty());
	}

	/**
	 * Checks that adding some unhappy people to the happy population will fail
	 * validation.
	 */
	@Test
	public void failTest_Some()
	{
		population.add(happy(), unhappy(), unhappy(), happy());

		List<ConstraintViolation> list = sut.validate(population);

		assertFalse("This population should not be all happy", list.isEmpty());
		assertEquals(1, list.size());
		ConstraintViolation violation = list.get(0);
		assertNull(violation.getCauses());
		assertEquals(population.people.get(1), violation.getInvalidValue());
	}

	/**
	 * Checks that adding some unhappy people to the happy population will fail
	 * validation.
	 */
	@Test
	public void failTest_All()
	{
		population.add(unhappy());

		List<ConstraintViolation> list = sut.validate(population);

		assertFalse("This population should not be all happy", list.isEmpty());
		assertEquals(1, list.size());
		ConstraintViolation violation = list.get(0);
		assertNull(violation.getCauses());
		assertEquals(population.people.get(0), violation.getInvalidValue());
	}

	/**
	 * Sample population consisting of people.
	 * 
	 * @author anydoby
	 *
	 */
	public static class HappyPopulation
	{

		@AssertNull(target = "jxpath:.[happy = false()]")
		List<Person> people = new ArrayList<NoTargetAccessorTest.Person>();

		/**
		 * @return happy people
		 */
		public List<Person> getPeople()
		{
			return people;
		}

		/**
		 * @param p
		 */
		public void add(Person... p)
		{
			people.addAll(asList(p));
		}

	}

	/**
	 * @author anydoby
	 *
	 */
	public static class Person
	{
		private boolean happy;

		/**
		 * @return <code>true</code> for happy people
		 */
		public boolean isHappy()
		{
			return happy;
		}

		/**
		 * @return a new happy person
		 */
		public static Person happy()
		{
			Person person = new Person();
			person.happy = true;
			return person;
		}

		/**
		 * @return a new unhappy person
		 */
		public static Person unhappy()
		{
			Person person = new Person();
			return person;
		}

		@Override
		public String toString()
		{
			return isHappy() ? "happy" : "unhappy";
		}
	}

}
