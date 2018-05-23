import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

public class Receive extends CyclicBehaviour{
    /**
     * Getting the sender convintion and his company value map from a msg
     */

    User a = (User) myAgent;

    public Receive(Agent a) {
        super(a);
    }

    public void action() {
        //MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = myAgent.receive();

        if(msg != null) {
            AID sender = msg.getSender();

            ArrayList<Integer> senderOV = new ArrayList<Integer>();
            int senderDeg = 0;

            try {
                senderOV = (ArrayList<Integer>) msg.getContentObject();
                senderDeg = senderOV.get(User.companies);
                senderOV.remove(User.companies);
            }
            catch (UnreadableException e) {e.printStackTrace();}

            /*System.out.println(a.getAID().getLocalName() +
                    " sender: " + sender.getLocalName() + " senderOV: " + senderOV +
            "senderDeg " + senderDeg);*/

            int index = a.compareVectors(a.getOpinionVector() , senderOV);

            double prob =  (double) senderDeg /(a.getDegree() + senderDeg);

            if(index != -1 && Math.random() <= prob ){
                //System.out.println(a.getAID().getLocalName() + " i'm changing OP");
                a.adjustVector(index , senderOV);
            }else{
                //System.out.println(a.getAID().getLocalName() + "i'm NOT changing OP");
            }



        }

        else {
            block();
        }
    }
}