import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import java.util.Arrays;

public class StepsBehaviour extends Behaviour {
    private int step = 0;
    public StepsBehaviour(Agent a) {
        super(a);
    }
    User a = (User) myAgent;
    public void action(){
        switch (step){
            case 0:
                a.configure();
                a.saveOpinion();
                step++;
                break;
            case 1:
                a.register();

                int p = User.inclDistr[a.getInclination()+1];
                User.inclDistr[a.getInclination()+1] = p+1;

                System.out.println("UserCounter:" + User.userCounter);
                User.userCounter++;
                step++;
                break;
            case 2:
                if(User.userCounter == User.userNumber){
                    /*System.out.println(a.getAID().getLocalName() + "\n" + "\t"
                            + " Deg= " + a.getDegree()
                            + " Incl= " + a.getInclination() + "\n" + "\t"
                            + " oVec= " + a.getOpinionVector());*/
                    a.setCallOut(a.makeCallOut());
                    step++;
                }
                break;
            case 3:
                System.out.print("Inclination Distribution: " );
                System.out.println(Arrays.toString(User.inclDistr));
                /*if(a.getAID().getLocalName() == "User" + (User.userNumber -1)){

                }*/
                //User.userCounter++;
                step++;
                break;
            case 4:
                a.sendMsg();
                User.userCounter1++;
                step++;
                break;
            case 5:
                if(User.userCounter1 == User.userNumber){
                    System.out.println(a.getAID().getLocalName() + " verified condition");
                    a.addBehaviour(new Receive(a));
                    step++;
                }
                break;

        }
    }
    /*public StepsBehaviour() {
        super();
        // TODO Auto-generated constructor stub
    }*/

    public boolean done() {
        return step == 6;
    }
}
