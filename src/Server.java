import jade.core.Agent;

//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class Server extends Agent{

    public static int serverCounter=0;
    private double[][] adjMatrix;

    protected void setup(){
        System.out.println("Server says that Users are " + User.userNumber);
        configure();
    }

    protected void configure(){

    }
}