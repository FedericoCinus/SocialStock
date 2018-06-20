import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;

public class StepsBehaviour extends Behaviour {
    private int step = 0;
    public StepsBehaviour(Agent a) {
        super(a);
    }
    User a = (User) myAgent;
    public void action(){
        switch (step){
            case 0:
                a.saveOpinion();
                //System.out.println("I'm " + a.getAID().getLocalName() + " my day is : " + a.uDays);
                //System.out.println("case0 step is  " + step );
                step++;
                break;
            case 1:
                a.register();
                int p = User.inclDistr[a.getInclination()+1];
                User.inclDistr[a.getInclination()+1] = p+1;
                User.userCounter++;
                if(User.userCounter == User.userNumber-1) {
                    User.userCounterD = 0;
                }
                //System.out.println("case1 step is  " + step );
                step++;
                break;
            case 2:
                if(User.userCounter == User.userNumber){
                    a.setCallOut(a.makeCallOut());
                    a.writeLineInt();
                    //System.out.println("case2 step is  " + step );
                    step++;
                }

                break;
            case 3:
                //System.out.print("Inclination Distribution: " );
                step++;
                break;
            case 4:
                a.sendMsg();
                User.userCounter1++;
                //System.out.println("case4 step is  " + step );
                step++;
                break;
            case 5:
                if(User.userCounter1 == User.userNumber){
                    a.addBehaviour(new Receive(a));
                    try{
                        DFService.deregister(a);
                    }catch(FIPAException fe){fe.printStackTrace();}
                    if(User.userCounterD == User.userNumber-1){
                        //System.out.println(a.getAID().getLocalName() + " is the one");
                        System.out.println("day : "+ a.uDays );

                        //File.csv filling
                        a.writeLines();

                        User.userCounter = 0;
                        User.userCounter1 = 0;
                        for(int i=0; i<3; i++)User.inclDistr[i] = 0;
                    }
                    User.userCounterD++;
                    a.uDays++;
                    //System.out.println("case5 step is  " + step );
                    step++;
                }
                break;
            case 6:
                if(User.userCounterD == User.userNumber){
                    if(a.uDays==User.days+1){
                        //System.out.println(a.getAID().getLocalName() + " Last day");
                        //System.out.println("case6 step is  " + step );
                        step++;
                    }
                    else{
                        a.callOut.clear();
                        a.callOutInt.clear();
                        step=0;
                    }
                }
                break;

        }
    }
    /*public StepsBehaviour() {
        super();
        // TODO Auto-generated constructor stub
    }*/

    public boolean done() {
        return step == 7;
    }
}
