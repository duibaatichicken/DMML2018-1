/* this just makes sure the data is in our desired format */
import java.io.*;
public class datatest
{
    public static void main(String args[]) throws IOException
    {
        String line = "";
	BufferedReader br = new BufferedReader (new FileReader ("connect-4.data"));
	line = br.readLine();
	System.out.println(line);
    }
}
		
	
