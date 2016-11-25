import soot.*;
import soot.util.*;
import java.util.*;

public class Hierarchy {

    // this hash table contains a pair (C,X) for each class C in
    // allClasses. X is a Hashset of all non-abstract classes that are
    // direct or indirect subclases of C (including C itself)
    private Hashtable tbl = new Hashtable();
    
    // all classes in the hierarchy
    public Set allClasses() { return tbl.keySet(); }

    public void initialize(Chain allClasses) {
	// constructs the necessary data structures for method
	// potentialReceiverClasses. More precisely, constructs all
	// pairs (C,X) in tbl
	
	// first, create an empty set for each class in allClasses
	for (Iterator it = allClasses.iterator(); it.hasNext();) 
	    tbl.put(it.next(),new HashSet());

	// next, for each non-abstract class C, recursively traverse
	// all superclasses of C and add C to their sets
	for (Iterator it = allClasses.iterator(); it.hasNext();) {
	    SootClass c = (SootClass) it.next();
	    if (notAbstract(c)) traverse(c,c);
	}
    }

    // recursive traversal of superclasses and superinterfaces
    private void traverse(SootClass sub, SootClass supr)
    {
	
		// sub is a subclass of supr
		
		// first, add sub to the set for supr
		((HashSet)tbl.get(supr)).add(sub);
	
		// TODO: traverse parent classes/interfaces of supr
		// (recursion).  to find whether supr has a superclass, use
		// method hasSuperclass(). to get the superclass, use method
		// getSuperclass(). For the interfaces implemented by supr,
		// use getInterfaces() to get a set, and then iterator() to
		// traverse it.
	
		// YOUR CODE HERE
		try
		{
			for(Object obj : supr.getInterfaces())
			{
				SootClass inter = (SootClass)obj;
				this.traverse(sub, inter);
			}
			
			if(supr.hasSuperclass())
			{
				this.traverse(sub, supr.getSuperclass());
			}
		}
		catch(Exception ex)
		{
			System.err.println("Exception " + ex.getClass().toString() + ": " + ex.getMessage());
		}
	
    }

    public HashSet possibleReceiverClasses(SootClass static_class) {

	// this method answers the following quesiton: if the
	// compile-time receiver class is static_class, what could be
	// the potential run-time receiver classes? the method returns
	// the set of all non-abstract classes that are direct or
	// indirect subclasses of static_class.  if static_class is a
	// non-abstract class, it is also included in the set. keep in
	// mind that static_class could represent an interface; in
	// this case we need to return all non-abstract classes that
	// implement that interface - directly or indirectly through
	// superclasses. 

	return (HashSet)tbl.get(static_class);
    }

    // this method simulates the effects of the virtual dispatch
    // performed by the JVM at run time. 
    public SootMethod virtualDispatch(SootMethod static_target,
				      SootClass receiver_class) {

	// TODO: implement this method. starting from receiver_class,
	// traverse the superclasses and look for a method for which
	// getSubSignature() is the same as
	// static_target.getSubSignature(). continue the traversal
	// until a match is found or hasSuperclass() returns false. If
	// you don't find a match, something is wrong.

	// YOUR CODE HERE. For now, the method just returns
	// (incorrectly) the static target method.
	try
	{
		SootClass cla = receiver_class;
		while(cla.hasSuperclass())
		{
			for(Object obj : cla.getMethods())
			{
				SootMethod method = (SootMethod)obj;
				if(method.getSubSignature().compareTo(static_target.getSubSignature()) == 0)
				{
					return method;
				}
			}
			cla = cla.getSuperclass();
		}
		for(Object obj : cla.getMethods())
		{
			SootMethod method = (SootMethod)obj;
			if(method.getSubSignature().compareTo(static_target.getSubSignature()) == 0)
			{
				return method;
			}
		}
	}
	catch(Exception ex)
	{
		System.err.println("Exception " + ex.getClass().toString() + ": " + ex.getMessage());
	}
	
	System.err.println("No method was found.");
	return static_target;
    }

    public boolean notAbstract(SootClass c) {
	return  ! ( c.isInterface() || 
		    Modifier.isAbstract(c.getModifiers()));
    }

    public boolean notLibrary(SootClass c) {
	String n = c.getName();
	return ! (n.startsWith("java.") || 
		  n.startsWith("javax.") ||
		  n.startsWith("sun."));
    }

    public boolean notLibrary(SootMethod m) {
	return notLibrary(m.getDeclaringClass());
    }
}
