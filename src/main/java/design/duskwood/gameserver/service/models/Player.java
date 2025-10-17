package design.duskwood.gameserver.service.models;

import lombok.Data;

@Data
public class Player {

  //inputs
  private boolean up;
  private boolean down;
  private boolean left;
  private boolean right;

  //data
  private double x;
  private double y;
  private double vx;
  private double vy;

  public Player() {
    this(0d, 0d, 0d, 0d);
  }

  public Player(double x, double y, double vx, double vy) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
  }

  public void incrementVx(double value) {
    vx += value;
  }

  public void incrementVy(double value) {
    vy += value;
  }

  public void decrementVx(double value) {
    vx -= value;
  }

  public void decrementVy(double value) {
    vy -= value;
  }

  public void applyVelocity() {
    x += vx;
    y += vy;
  }

  public boolean isMoving() {
    return this.vx != 0 || this.vy != 0;
  }


}
