package util;
public class HelperFunctionsDazzle
{
    /************************* *************************/   
    
    /**
     * @description helper function to convert b,o,x to numbers
     */
    public static int attrToInt(String attr)
    {
	if (attr.equals("b") || attr.equals("draw"))
	    return 0;
	else if (attr.equals("o") || attr.equals("win"))
	    return 1;
	else if (attr.equals("x") || attr.equals("loss"))
	    return 2;
	else
	    throw(new RuntimeException("Invalid attribute value"));
    }
    /************************* *************************/   
    
    /**
     * @description helper function to convert classes to numbers
     */
    public static int classToInt(String cl)
    {
	if (cl.equals("draw"))
	    return 0;
	else if (cl.equals("win"))
	    return 1;
	else if (cl.equals("loss"))
	    return 2;
	else
	    throw (new RuntimeException("Invalid class"));
    }
    
}
