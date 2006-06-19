package net.sf.oval;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.oval.exceptions.ReflectionException;

public interface ParameterNameResolver
{
	String[] getParameterNames(Method method) throws ReflectionException;

	String[] getParameterNames(Constructor constructor) throws ReflectionException;
}
