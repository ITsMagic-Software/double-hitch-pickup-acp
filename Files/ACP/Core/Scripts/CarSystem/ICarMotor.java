public interface ICarMotor{
    public void addWheel(ICarWheel v);
    public void setSteer(float v);
    public void setAceleration(float v);
    public float getSpeed();
    public void setHandBrake(float v);
    public float getSpeedPercentage();
}
