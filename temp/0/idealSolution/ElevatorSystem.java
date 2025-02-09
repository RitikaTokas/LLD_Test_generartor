public class ElevatorSystem implements ProblemInterface{
  private Elevator elevator;
  public ElevatorSystem() { this.elevator = new Elevator(); }
  public void processRequest(int floor) { elevator.moveToFloor(floor); }
  public int getCurrentFloor() { return elevator.getCurrentFloor(); }
}