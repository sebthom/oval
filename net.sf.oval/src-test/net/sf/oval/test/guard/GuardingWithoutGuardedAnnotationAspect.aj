package net.sf.oval.test.guard;

import net.sf.oval.Guard;
import net.sf.oval.IsGuarded;
import net.sf.oval.Validator;
import net.sf.oval.aspectj.GuardAspect;
import net.sf.oval.aspectj.ParameterNameResolverAspectJImpl;
import net.sf.oval.test.guard.GuardingWithoutGuardedAnnotationTest.TestEntity;

public aspect GuardingWithoutGuardedAnnotationAspect extends GuardAspect
{
	protected pointcut scope(): within(TestEntity);

	declare parents: TestEntity implements IsGuarded;
	
	public final static Validator validator = new Validator();
	public final static Guard guard = new Guard(validator);

	static GuardingWithoutGuardedAnnotationAspect INSTANCE;

	public GuardingWithoutGuardedAnnotationAspect()
	{
		super(guard);
		INSTANCE = this;
		validator.setParameterNameResolver(new ParameterNameResolverAspectJImpl());
	}
}
