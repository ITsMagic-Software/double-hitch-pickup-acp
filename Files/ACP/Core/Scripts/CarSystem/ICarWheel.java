/**
 * @Author ITsMagic
*/
public interface ICarWheel{ 
   public void setTorque(float v);
   public void setBrake(float v);
   public void setSteer(float v);
   public void emitSkid(float i);
   public void setHandBrake(float v);
   public void emitThrottleParticle(float i, boolean forward);
   public boolean isTorqueWheel();
   public float getHorizontalPivot();
}
