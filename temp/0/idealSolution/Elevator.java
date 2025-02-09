public class Elevator {
  private int currentFloor;
  public Elevator() { this.currentFloor = 0; }
  public void moveToFloor(int floor) { this.currentFloor = floor; }
  public int getCurrentFloor() { return currentFloor; }
}