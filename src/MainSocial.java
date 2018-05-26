import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;

//linea di commento

public class MainSocial {
    public static void main(String[] args) {
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MTPS,"jade.mtp.http.MessageTransportProtocol(http://localhost:7778)");
        p.setParameter(Profile.GUI , "false");
        ContainerController cc = runtime.createMainContainer(p);

        User.days = 10;
        User.companies = 5;
        User.opinionRange = 10;
        User.userNumber = 10;

        User.title =  "c" + User.companies + "r" + User.opinionRange + "n" + User.userNumber + ".txt";
        User.titleInc = "c" + User.companies + "r" + User.opinionRange + "n" + User.userNumber + "Inc.txt";
        User.titleInt = "data/c" + User.companies + "r" + User.opinionRange + "n" + User.userNumber + "Int.txt";


        for(int i=0 ; i<User.userNumber ; i++ ) {
            User.opinionMatrix.add(new ArrayList<Double>());

        }

        try {
            for(int i = 0; i < User.userNumber ; i++) {
                AgentController ac = cc.createNewAgent("User"+i , "User", args);
                ac.start();
                try {
                    Thread.sleep(100);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (StaleProxyException e) {
            e.printStackTrace();
        }


        //System.out.println("OPINION MATRIX IS:");
        //System.out.println(User.opinionMatrix);
    }
}