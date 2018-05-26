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
    public static FileWriter dataInc = null;

    public static  String title;
    public static String titleInc;
    public static int days;

    public static int companies;
    public static int opinionRange;
    public static int userNumber;
    public static int userCounter = 0;
    public static int userCounter1 = 0;
    public static int userCounterD = 0;
    public static int[] inclDistr = {0,0,0};
    public static ArrayList<ArrayList<Double>> opinionMatrix = new ArrayList<ArrayList<Double>>();


    public int uDays = 1;
    ArrayList<Integer>  opinionVector = new ArrayList<Integer>();
    public int degree;
    public ArrayList<AID> callOut = new ArrayList<AID>();

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
        double gamma = 2.5;
        this.degree = (int) Math.pow(1-Math.random(),1/(1-gamma));

        System.out.println(getAID().getLocalName() + " : my opinion vector is");
        System.out.println(this.opinionVector);
        System.out.println(getAID().getLocalName() + " : my degree is");
        System.out.println(this.degree);
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
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName("My inclination is " + this.getInclination());
        sd.setType("");
        template.addServices(sd);

        ArrayList<DFAgentDescription> result = new ArrayList<DFAgentDescription>() ;

        try {
            DFAgentDescription[] prova = DFService.search(this, template);
            result = new ArrayList<DFAgentDescription>(Arrays.asList(prova));
        }
        catch (FIPAException e){
            e.printStackTrace();
        }

        ArrayList<AID> resultAID = new ArrayList<AID>();
        for(int i = 0; i < result.size(); i++){
            resultAID.add(result.get(i).getName());
        }
        ArrayList<String> resultLocal = new ArrayList<String>();
        for(int i = 0; i < result.size(); i++){
            resultLocal.add(resultAID.get(i).getLocalName());
        }

        try{
            resultAID.remove(resultAID.indexOf(this.getAID()));
        }catch(IndexOutOfBoundsException e){System.out.println("I'm : " +this.getAID().getLocalName() + "list " +  resultLocal);}
        Collections.shuffle(resultAID);

        /*System.out.println(getAID().getLocalName() + "\n" + "\t"
                + " Deg= " + getDegree()
                + " Incl= " + getInclination() + "\n" + "\t"
                + " oVec= " + getOpinionVector());*/


        if(this.getDegree()>=resultAID.size()){
            //System.out.println(getAID().getLocalName() +" ResultAID size is:" + resultAID.size());
            return resultAID;
        }

        ArrayList<AID> resultAID2 = new ArrayList<AID>();
        for(int i = 0; i < this.getDegree(); i++) {
            resultAID2.add(resultAID.get(i));
        }
        //System.out.println(getAID().getLocalName() +" ResultAID2 size is:" + resultAID2.size());
        return resultAID2;
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

        //INCLINATION DISTRIBUTION txt
        try{
            dataInc = new FileWriter(titleInc, true);

            PrintWriter out = new PrintWriter(dataInc);

            int size = User.inclDistr.length;
            for(int i = 0 ; i < size ; i++){
                out.print(User.inclDistr[i]);
                if(i != size -1){
                    out.print(",");
                }
            }
            out.print("\n");

            out.flush();
            out.close();
            dataInc.close();
        }catch(IOException e){e.printStackTrace();}
    }

}