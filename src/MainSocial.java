import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

//linea di commento

public class MainSocial {
    public static void main(String[] args) {
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MTPS,"jade.mtp.http.MessageTransportProtocol(http://localhost:7778)");
        p.setParameter(Profile.GUI , "false");
        ContainerController cc = runtime.createMainContainer(p);


        //INPUT PARAMETERS
        User.days = 50;
        User.companies = 3;
        User.opinionRange = 5;
        User.userNumber = 100;
        User.gamma = 2.5; // 0. uniform ; [2:3] scale-free
        User.percDiff = 0.2;

        // Setting parameters for influencing given percentage of users
        User.influencingPerc = 0.;
        User.influencingCompany = 2;
        User.influencingType = "Bad";
        if(User.influencingType=="Bad") {
            User.influencingValue = 0;
        }
        if(User.influencingType=="Good"){
            User.influencingValue = User.opinionRange;
        }

        User.title =  "data/c" + User.companies + "r" + User.opinionRange + "n" + User.userNumber + ".txt";
        User.titleInt = "data/c" + User.companies + "r" + User.opinionRange + "n" + User.userNumber + "Int.txt";

        try{
            User.data= new FileWriter(User.title);

            PrintWriter out = new PrintWriter(User.data);

            out.print("OpinionMatrix");
            out.print("\n");

            out.flush();
            out.close();
            User.data.close();
        }catch(IOException e){e.printStackTrace();}


        try{
            User.dataInt = new FileWriter(User.titleInt);

            PrintWriter out = new PrintWriter(User.dataInt);

            out.print("uDays");
            out.print("-");

            out.print("ind");
            out.print("-");

            out.print("incl");
            out.print("-");

            out.print("deg");
            out.print("-");

            out.print("callout");
            out.print("\n");

            out.flush();
            out.close();
            User.dataInt.close();
        }catch(IOException e){e.printStackTrace();}


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