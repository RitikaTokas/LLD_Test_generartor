public class Main {
  public static void main(String[] args) {
    ElevatorSystem system = new ElevatorSystem();
    system.processRequest(5);
    System.out.println(system.getCurrentFloor());
  }
}