import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;

public class ChaWriter {

    // directory containing the output files
    private String output_dir;

    // file storing info about call sites inside non-library methods
    private BufferedWriter call_file;

    // auxiliary variable used to filter out call sites inside library
    // methods
    private boolean inside_nonlib_method = false;

    // A Hierarchy object providing necessary info for the printing
    private Hierarchy hierarchy;
    

    // --------------------------------
    public ChaWriter(String dir_name, Hierarchy h) { 

	output_dir = dir_name; 
	hierarchy = h;

	// open file to store info about all calls in non-library methods
	try {
	    call_file =
		new BufferedWriter(new FileWriter(output_dir + "/calls"));
	} catch (Exception e) {
	    System.out.println("OOPS! " + e);
	}
    }
    
    
    public void writeMethodInfo(Set reachable) {
	
	// open files to store the info
	try {
	    BufferedWriter file =
		new BufferedWriter(new FileWriter(output_dir + "/rmethods_all"));
	    BufferedWriter file_nl =
		new BufferedWriter(new FileWriter(output_dir + "/rmethods"));
	
	    file.write("Total num reachable methods: " + reachable.size() + "\n");
	    for (Iterator it = reachable.iterator(); it.hasNext();) {
		SootMethod m = (SootMethod) it.next();
		file.write(m + "\n");
		if (hierarchy.notLibrary(m)) 
		    file_nl.write(m + "\n");
	    }
	    file.close();
	    file_nl.close();
	} catch (Exception e) {
	    System.out.println("OOPS! " + e);
	}
    }
    public void writeHierarchyInfo() {

	// open a file to store the info
	try {
	    BufferedWriter file =
		new BufferedWriter(new FileWriter(output_dir + "/hier_all"));
	    BufferedWriter file_nl =
		new BufferedWriter(new FileWriter(output_dir + "/hier"));
	
	    Set C = hierarchy.allClasses();
	    file.write("Total num classes: " + C.size() + "\n");
	    for (Iterator it = C.iterator(); it.hasNext();) {
		SootClass c = (SootClass) it.next();
		file.write(c + "," + 
			   hierarchy.possibleReceiverClasses(c).size() + "\n");
		if (hierarchy.notLibrary(c)) 
		    file_nl.write(c + "," + 
				  hierarchy.possibleReceiverClasses(c).size() + 
				  "\n");
	    }
	    file.close();
	    file_nl.close();
	} catch (Exception e) {
	    System.out.println("OOPS! " + e);
	}
    }

    public void startNewMethod(SootMethod m) {
	inside_nonlib_method = hierarchy.notLibrary(m);
	if (inside_nonlib_method) 
	    try {
		call_file.write("\n===== Method " + m + "\n");
	    } catch (Exception e) {
		System.out.println("OOPS! " + e);
	    }
    }

    public void writeSimpleCall(InvokeExpr call) {
	if (inside_nonlib_method) 
	    try {
		call_file.write("[S] " + call + "\n");
	    } catch (Exception e) {
		System.out.println("OOPS! " + e);
	    }
    }

    public void writeComplexCall(InvokeExpr call, 
				 int num_rcv_classes, 
				 int num_target_methods) {
	if (inside_nonlib_method) 
	    try {
		call_file.write("[C] " + call + "," + num_rcv_classes + "," + 
				num_target_methods + "\n");
	    } catch (Exception e) {
		System.out.println("OOPS! " + e);
	    }
    }

    public void writeTarget(SootMethod m) {
	if (inside_nonlib_method) 
	    try {
		call_file.write("     " + m + "\n");
	    } catch (Exception e) {
		System.out.println("OOPS! " + e);
	    }
    }

    public void done() {
	try {
	    call_file.close();
	} catch (Exception e) {
	    System.out.println("OOPS! " + e);
	}
    }

}
