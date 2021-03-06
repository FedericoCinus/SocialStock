import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class User extends Agent{

    // FIle.csv creation
    public static FileWriter data = null;
    public static FileWriter dataInt = null;
    public static FileWriter dataInt2 = null;
    public static String stringDataInt = "";


    public static String title;
    public static String titleInt;
    public static String titleInt2;

    public static int days;


    public static int companies;
    public static int opinionRange;
    public static int userNumber;
    public static double gamma;
    public static double percDiff;

    public static int userCounter = 0;
    public static int userCounter1 = 0;
    public static int userCounterD = 0;
    public static int[] inclDistr = {0,0,0};
    public static ArrayList<ArrayList<Double>> opinionMatrix = new ArrayList<ArrayList<Double>>();

    //Influencing parameters
    public static Double influencingPerc;
    public static int influencingCompany;
    public static int influencingValue;
    public static String influencingType;

    public int uDays = 1;
    ArrayList<Integer>  opinionVector = new ArrayList<Integer>();
    public int degree;
    public ArrayList<AID> callOut = new ArrayList<AID>();
    public ArrayList<String> callOutInt = new ArrayList<String>();


    public ArrayList<Integer> getOpinionVector() {
        return opinionVector;
    }
    public void setOpinionVector(ArrayList<Integer> opinionVector) {
        this.opinionVector = opinionVector;
    }
    public int getDegree() {
        return degree;
    }
    public void setDegree(int degree) {
        this.degree = degree;
    }
    public ArrayList<AID> getCallOut() {
        return callOut;
    }
    public void setCallOut(ArrayList<AID> callOut) {
        this.callOut = callOut;
    }


    protected void setup(){
        configure();
        addBehaviour(new StepsBehaviour(this));
    }

    protected void configure(){

        //creating an opinionVector ; params are described in MainSocialNetwork
        ArrayList<Integer>  vec = new ArrayList<Integer>();
        for(int i = 0; i < User.companies ; i++){
            double result = Math.random()* User.opinionRange;
            int result2 = (int) result;
            vec.add(i , result2);
        }
        this.opinionVector = vec;
        //defining the degree of each User as the total number of User times random value [0,1]
        //this.degree = (int) (Math.random() * userNumber);

        double deg = Math.pow(1-Math.random(),1/(1-gamma));
        this.degree = (gamma == 0.) ? (int) (deg*userNumber) : (int) deg;


        System.out.println( getAID().getLocalName() );
        
        /*System.out.println(getAID().getLocalName() + " : my opinion vector is");
        System.out.println(getAID().getLocalName() + " : my opinion vector is");
        System.out.println(this.opinionVector);
        System.out.println(getAID().getLocalName() + " : my degree is");
        System.out.println(this.degree);
        */
    }

    protected int getFavoriteCompany(){
        int maxIndex = opinionVector.indexOf(Collections.max(opinionVector));

        //System.out.println(getAID().getLocalName() + " : mi favorite company is " + maxIndex);

        return maxIndex;
    }

    protected int getInclination() {
        ArrayList<Integer> vec = getOpinionVector();
        double result=0.;

        double var = (double)(Math.pow(opinionRange, 2) - 1)/(12*companies);
        double mu  = (double)(opinionRange-1)/2;
        double q1 = (double)(mu + Math.sqrt(var)*(-0.4307273));
        double q2 = opinionRange-1-q1;

        for(int i = 0; i< User.companies; i++) {
            result +=  vec.get(i);
        }
        result = (double) result/User.companies;

        if(result<= q1){
            return -1;
        }
        else if(result<= q2){
            return 0;
        }
        else {
            return 1;
        }
    }

    protected void register(){
        /**
         * The Agent registers its Preferred Company on the Directory Facilitator
         * It provides also its degree
         */
        ServiceDescription sdDegree = new ServiceDescription();
        ServiceDescription sdCompany = new ServiceDescription();

        sdDegree.setName("Degree = " + this.getDegree());
        sdCompany.setName("My inclination is " + this.getInclination());

        sdDegree.setType("");
        sdCompany.setType("");


        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());

        dfd.addServices(sdDegree);
        dfd.addServices(sdCompany);

        try {
            DFService.register(this, dfd);
            //System.out.println("Hi pacc I'm " + this.getAID().getLocalName() + " and I'm registered! ");
        }
        catch (FIPAException e) {
            e.printStackTrace();
        }

    }



    protected ArrayList<AID> makeCallOut() {
        /*
         * creating a list for comunication between users with a certain percentage of
         * same opinion (perc)
         */

        //SAME INCLINATION
        int my_incl = this.getInclination();
        ArrayList<AID> resultAID0 = genResultAID(this.getInclination());

        //OTHER INCLINATIONS
        int other_incl1 = ((my_incl + 2) % 3) - 1;
        int other_incl2 = ((my_incl + 3) % 3) - 1;

        ArrayList<AID> resultAID1 = genResultAID(other_incl1);
        ArrayList<AID> resultAID2 = genResultAID(other_incl2);
        ArrayList<AID> resultAID12 = resultAID1;
        for(int i = 0 ; i < resultAID2.size() ; i++){
            resultAID12.add(resultAID2.get(i));
        }
        Collections.shuffle(resultAID12);

        int other = (int) (percDiff * this.getDegree());
        int same = this.getDegree() - other;

        ArrayList<AID> resultAID = new ArrayList<AID>();
        //CASE1 ENOUGH SAMES AND ENOUGH OTHERS
        if(same <= resultAID0.size() && other <= resultAID12.size()){
            for(int i = 0 ; i < same ; i++){
                resultAID.add(resultAID0.get(i));
            }
            for(int i = 0 ; i < other ; i++){
                resultAID.add(resultAID12.get(i));
            }
            ///////////////////////////////////////////////
            for(int i = 0 ; i < resultAID.size() ; i++){
                String name = resultAID.get(i).getLocalName();
                callOutInt.add(name.substring(4));
            }
            ///////////////////////////////////////////////
            //writing file
            //writeLineInt();
            return(resultAID);
        }else{
            //CASE2 NOT ENOUGH SAMES
            if(same >= resultAID0.size()){
                resultAID = resultAID0; //same == resultAID0.size()
                other = this.getDegree() - resultAID0.size();
                for(int i = 0 ; i < other ; i++){
                    resultAID.add(resultAID12.get(i));
                }
                ////////////////////////////////////////////////
                for(int i = 0 ; i < resultAID.size() ; i++){
                    String name = resultAID.get(i).getLocalName();
                    callOutInt.add(name.substring(4));
                }
                ////////////////////////////////////////////////
                //writing file
                //writeLineInt();
                return(resultAID);
            }
            //CASE3 NOT ENOUGH OTHERS
            else{
                resultAID = resultAID12; // other = resultAID12.size()
                same = this.getDegree() - resultAID12.size();
                for(int i = 0 ; i < same ; i++){
                    resultAID.add(resultAID0.get(i));
                }
                /////////////////////////////////////////////////
                for(int i = 0 ; i < resultAID.size() ; i++){
                    String name = resultAID.get(i).getLocalName();
                    callOutInt.add(name.substring(4));
                }
                /////////////////////////////////////////////////
                //writing file
                //writeLineInt();
                return(resultAID);
            }

        }
    }

    protected ArrayList<AID> genResultAID(int incl){

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName("My inclination is " + incl);
        sd.setType("");
        template.addServices(sd);

        ArrayList<DFAgentDescription> result = new ArrayList<DFAgentDescription>();
        try {
            DFAgentDescription[] prova = DFService.search(this, template);
            result = new ArrayList<DFAgentDescription>(Arrays.asList(prova));
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        ArrayList<AID> resultAID = new ArrayList<AID>();
        for (int i = 0; i < result.size(); i++) {
            resultAID.add(result.get(i).getName());
        }
        Collections.shuffle(resultAID);
        return resultAID;
    }



    protected void sendMsg() {

        ArrayList<Integer> info = opinionVector;
        info.add(getDegree());
        //System.out.println(getAID().getLocalName() + " info : " + info);


        for(AID receiver : getCallOut()) {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

            try {
                msg.setContentObject(info);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //System.out.println("I'm " + getAID().getLocalName() + " sending msg to " + receiver.getLocalName());
            msg.addReceiver(receiver);
            send(msg);
        }
    }



    protected int compareVectors(ArrayList<Integer> vec1 , ArrayList<Integer> vec2){

        //compares the two opinionvector of sender and receiver user
        //returns an index-entry at which the two vector differ
        //if the vectors are equal the method returns -1


        ArrayList<Integer> difIndexes = new ArrayList<Integer>();

        int a = (int) (Math.random()*companies);

        for(int i = 0; i< User.companies ; i++){

            while(difIndexes.contains(a) && difIndexes.size() < companies){
                a = (int) (Math.random()*companies);
            }

            if(vec1.get(a)!= vec2.get(a)){
                return a;
            }else{
                difIndexes.add(a);
            }

        }
        return -1;
    }

    protected void adjustVector(int index , ArrayList<Integer> senderOV ){
        ArrayList<Integer> newOV = this.getOpinionVector();
        newOV.set(index , senderOV.get(index));
        this.setOpinionVector(newOV);
    }


    protected void saveOpinion(){
        ArrayList<Double> row = new ArrayList<Double>();
        for(int i=0 ; i<companies ; i++) {
            row.add(opinionVector.get(i) - (double) (opinionRange-1)/2);
        }
        int index = Integer.parseInt(getAID().getLocalName().substring(4));
        opinionMatrix.set(index,row);
    }


    protected void writeLines(){

        //OPINION MATRIX txt
        try{
            data = new FileWriter(title, true);

            PrintWriter out = new PrintWriter(data);

            for(int i=0; i<userNumber; i++){
                ArrayList<Double> row = opinionMatrix.get(i);

                for(int j=0; j<companies; j++){
                    out.print( row.get(j) );
                    if(j != companies-1) {
                        out.print(",");
                    }
                }
                out.print("\n");
            }

            out.flush();
            out.close();
            data.close();
        }catch(IOException e){e.printStackTrace();}

        //writing stringDataInt in dataInt2;
        try{
            dataInt2 = new FileWriter(titleInt2, true);

            PrintWriter out = new PrintWriter(dataInt2);

            out.print(stringDataInt);
            out.flush();
            out.close();
            stringDataInt = "";
            dataInt2.close();
        }catch(IOException e){e.printStackTrace();}

    }


    protected synchronized void writeLineInt() {
        try {
            dataInt = new FileWriter(titleInt, true);

            PrintWriter out = new PrintWriter(dataInt);
            String index = getAID().getLocalName().substring(4);
            String line = uDays + ":" + index + ":" + getInclination() + ":" + getDegree() + ":" + callOutInt + "\n";
            User.stringDataInt += line;

            out.print(line);
            /*
            out.print(uDays);
            out.print(":");

            out.print(getAID().getLocalName().substring(4));
            out.print(":");

            out.print(getInclination());
            out.print(":");

            out.print(getDegree());
            out.print(":");

            out.print(callOutInt);
            out.print("\n");
            */


            out.flush();
            out.close();
            dataInt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}