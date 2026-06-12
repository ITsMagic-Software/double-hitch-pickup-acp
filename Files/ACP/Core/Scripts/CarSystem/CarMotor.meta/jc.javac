/** @Author ITsMagic */
public class CarMotor extends Component implements ICarMotor {

  private static final float TORQUE_MULTIPLIER = 25;

  private List<ICarWheel> wheelsList = new ArrayList();
  private Set<ICarWheel> wheelsSet = new HashSet();

  public boolean engineOn = true;

  public float horsePower = 110;
  public float maxRPM = 3500;
  public float maxSpeed = 150;
  public float maxReverseSpeed = 35;

  public float maxSteerAngle = 35;
  public float minSteerAngle = 25;
  public float steerLerp = 2f;

  public float brakeMultiplier = 1f;
  public float offBrake = 0.4f;
  public float handBrake = 0f;

  public UITextView carInfo;

  public Curve torqueCurve = new Curve();

  public boolean lockDifferential = false;

  private float currentSteerAngle;
  private float steer = 0;
  private float currentSpeed;
  private float aceleration = 1f;
  @AutoWired 
  private VehiclePhysics vp;
  private float speedPercentage = 0;

  /// Run only once
  @Override
  public void start() {}

  /// Repeat every frame
  @Override
  public void repeat() {
    currentSpeed = Math.abs(vp.getSpeedKMH()) * 1.5f;
    if (vp.getSpeedKMH() >= 0) {
      speedPercentage = currentSpeed / maxSpeed;
      speedPercentage = Math.clamp(0, speedPercentage, 1);
    } else {
      speedPercentage = currentSpeed / maxReverseSpeed;
      speedPercentage = Math.clamp(-1, speedPercentage, 0);
    }
    
    
    if (engineOn) {
      float at = calculateTorque();
      applyTorque(at);

      float ab = calculateBrake();
      applyBrake(ab);

      applyHandBrake(handBrake);
      calculateThrottleParticle();

      makeSteer();

      if (carInfo != null) {
        String i = "";
        i += "Speed:" + ((int) currentSpeed) + "\n";
        i += "Torque:" + ((int) at) + "\n";
        i += "Aceleration:" + aceleration + "\n";
        i += "Brake:" + ab + "\n";
        i += "Steer:" + ((int) currentSteerAngle) + "\n";
        carInfo.setText(i);
      }
    } else {
      applyBrake(offBrake);

      /*if (carInfo != null) {
        String i = "";
        i += "Engine off\n";
        i += "Steer:" + ((int) currentSteerAngle) + "\n";
        carInfo.setText(i);
      }*/
    }
  }

  void makeSteer() {
    float maxSteer = calculateSteer();
    float wantedSteer = maxSteer * -steer;
    float lp = steerLerp * 4f;
    currentSteerAngle = Math.lerpInSeconds(currentSteerAngle, wantedSteer, lp * maxSteerAngle);
    applySteer(currentSteerAngle);
  }

  void calculateThrottleParticle() {
    if (aceleration > 0) {
      float maxGripSpeed = (horsePower / maxSpeed) * 10f;
      float mspeed = aceleration * maxGripSpeed;
      float speedP = vp.getSpeedKMH() / mspeed;
      speedP = Math.clamp(0, speedP, 1);
      if (speedP < 1) {
        for (int x = 0; x < wheelsList.size(); x++) {
          ICarWheel w = wheelsList.get(x);
          w.emitThrottleParticle(1f, true);
        }
      }
    }
  }

  float calculateBrake() {
    if (aceleration < 0) {
      if (vp.getSpeedKMH() > 0) {
        return -brakeMultiplier * aceleration;
      }
    } else if (aceleration > 0) {
      if (vp.getSpeedKMH() < 0) {
        return brakeMultiplier * aceleration;
      }
    }
    return 0;
  }

  float calculateTorque() {
    if (aceleration >= 0) {
      if (speedPercentage < 1) {
        float c = torqueCurve.evaluate(speedPercentage);
        float t = horsePower * c * (1f - speedPercentage);

        return t * aceleration;
      } else {
        float p = speedPercentage - 1f;
        return -(horsePower * p);
      }
    } else if (aceleration < 0) {
      float speedPercentage = (-vp.getSpeedKMH() * 2) / maxReverseSpeed;
      speedPercentage = Math.clamp(0, speedPercentage, 1f);

      if (speedPercentage < 1) {
        float c = torqueCurve.evaluate(speedPercentage);
        float t = horsePower * c * (1f - speedPercentage);

        return t * aceleration;
      } else {
        float p = speedPercentage - 1f;
        return -(horsePower * p);
      }
    } 

    float p = speedPercentage;
    if (speedPercentage > 0) {
      return -(horsePower * p);
    } else {
      return 0;
    }
  }

  float calculateSteer() {
    if (vp.getSpeedKMH() < 0) {
      return maxSteerAngle;
    }
    float sp = 1f - Math.clamp(0, speedPercentage, 1);
    float maxSteer = ((maxSteerAngle - minSteerAngle) * sp) + minSteerAngle;
    return maxSteer;
  }

  /* Override */
  public void setSteer(float v) {
    this.steer = v;
  }

  /* Override */
  public void setAceleration(float v) {
    this.aceleration = v;
  }

  /* Override */
  public void setHandBrake(float v) {
    this.handBrake = v;
  }

  /* Override */
  public float getSpeed() {
    return currentSpeed;
  }

  /* Override */
  public void addWheel(ICarWheel v) {
    if (wheelsSet.contains(v)) return;
    wheelsSet.add(v);
    wheelsList.add(v);
  }

  private void applyTorque(float v) {
    float steer = -this.steer;
    int torqueWheelsCount = 0;
    float minHorizontalPivot = 0;
    float maxHorizontalPivot = 0;
    for (int x = 0; x < wheelsList.size(); x++) {
      ICarWheel w = wheelsList.get(x);
      if (w.isTorqueWheel()) {
        torqueWheelsCount++;
      }

      minHorizontalPivot = java.lang.Math.min(minHorizontalPivot, w.getHorizontalPivot());
      maxHorizontalPivot = java.lang.Math.max(maxHorizontalPivot, w.getHorizontalPivot());
    }
    v *= TORQUE_MULTIPLIER / ((float) torqueWheelsCount);
    for (int x = 0; x < wheelsList.size(); x++) {
      ICarWheel w = wheelsList.get(x);
      float p = w.getHorizontalPivot();

      float diff = 0;

      if (!lockDifferential) {
        if (p < 0) {
          diff = Math.abs(p) / Math.abs(minHorizontalPivot);
          if (steer < 0) {
            diff *= 1 + steer;
            // print (p + " - " + diff + " - " + steeringPercentage);
          }
        } else {
          diff = p / maxHorizontalPivot;
          if (steer > 0) {
            diff *= steer - 1f;
          }
        }
      } else {
        diff = 1;
      }
      
      w.setTorque(v * diff);
    }
  }

  private void applyBrake(float v) {
    for (int x = 0; x < wheelsList.size(); x++) {
      ICarWheel w = wheelsList.get(x);
      w.setBrake(v);
    }
  }

  private void applyHandBrake(float v) {
    for (int x = 0; x < wheelsList.size(); x++) {
      ICarWheel w = wheelsList.get(x);
      w.setHandBrake(v);
    }
  }

  private void applySteer(float v) {
    for (int x = 0; x < wheelsList.size(); x++) {
      ICarWheel w = wheelsList.get(x);
      w.setSteer(v);
    }
  }

  /* override */
  public float getSpeedPercentage() {
    return speedPercentage;
  }
}