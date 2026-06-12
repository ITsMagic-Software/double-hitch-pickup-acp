/** @Author ITsMagic */
public class CarWheel extends Component implements ICarWheel {

  @Order(idx = {1})
  public boolean torque = true;

  @Order(idx = {2})
  public boolean brake = true;

  @Order(idx = {3})
  public boolean steer = false;

  @Order(idx = {4})
  public boolean handBrake = true;

  @Order(idx = {5})
  public float runningFriction = 1f;

  @Order(idx = {6})
  public float handBrakingFriction = 0.5f;

  @Order(idx = {7})
  public float brakeForce = 10;

  @Order(idx = {8})
  public boolean invertSimulatedRotation = false;

  @Order(idx = {9})
  public float burnoutAngleIntensity = 500;

  @AutoWired
  private WheelSkidMark wsk;
  @AutoWired
  private VehicleWheel vw;
  @AutoWired
  private VehiclePhysics vp;
  private ICarMotor motor;

  @Order(idx = {9})
  public ObjectFile smokeParticleFile;

  private ParticleEmitter throttleParticle;
  private SpatialObject smokePO;

  private float activeBrake;
  private float activeHBrake;
  private float activeTorque;

  private float wantedSkid = 0;

  private float startFriction;
  private float emitThrrotleParticleTime = 0;

  private float horizontalPivot;

  /// Run only once
  @Override
  public void start() {
    motor = findMotor();
    startFriction = vw.getFrictionSlip();

    SpatialObject po = myObject.parent.instantiateAsChild(smokeParticleFile);
    smokePO = po;
    po.position = myObject.position;
    Quaternion rot = new Quaternion();
    rot.lookTo(myObject.back());
    po.globalRotation = rot;
    throttleParticle = po.findComponent(ParticleEmitter.class);
  }

  /// Repeat every frame
  @Override
  public void repeat() {
    if (motor != null) {
      motor.addWheel(this);
    }

    if (throttleParticle != null) {
      if (emitThrrotleParticleTime > 0) {
        emitThrrotleParticleTime -= Math.bySecond();
        throttleParticle.allowEmission = true;
        wantedSkid += 0.5f;
      } else {
        throttleParticle.allowEmission = false;
      }
    }

    if (wsk != null && wantedSkid > 0) {
      wsk.emit(wantedSkid);
      wantedSkid -= Math.bySecond(5f);
    }
    wantedSkid = Math.clamp(0, wantedSkid, 1);

    if (activeHBrake > 0) {
      vw.setFrictionSlip(handBrakingFriction * startFriction);
    } else {
      vw.setFrictionSlip(runningFriction * startFriction);
    }

    float blockRotI = activeHBrake + activeBrake;
    blockRotI = Math.clamp(0, blockRotI, 1);
    vw.setBlockRotation(blockRotI);

    calculatePivot();
    updateSmokeDir();
  }

  private Vector3 calculateBottomWheelLocalPos() {
      Vector3 p = myObject.position.copy();
      float y = p.getY();
      y -= vw.radius;
      p.setY(y);
      return p;
  } 

  private Vector3 calculateSteerVector() {
    Vector3 up = myObject.up();
    Vector3 right = myObject.getChildAt(0).right();
    Vector3 dir = up.cross(right);
    return dir;
  }

  private void updateSmokeDir() {
    Vector3 dir = calculateSteerVector();
    Quaternion rot = new Quaternion();
    rot.lookTo(dir);
    smokePO.globalRotation = rot;
    
    Vector3 p = calculateBottomWheelLocalPos();
    p.setY(p.getY() + 0.01f);
    smokePO.position = p;
  }

  private void calculatePivot() {
    SpatialObject car = myObject.mainParent;
    Vector3 myPos = myObject.globalPosition;
    Vector3 pivot = car.inverseTransformPoint(myPos);
    horizontalPivot = pivot.x;
  }

  /* override */
  public void setTorque(float v) {
    if (torque) {
      v = v * (1 - activeHBrake);
      activeTorque = v;
      vw.setTorque(v);
    }
  }

  /* override */
  public void setBrake(float v) {
    if (brake) {
      activeBrake = v;
      v = v + activeHBrake;
      vw.setBrake(v * brakeForce * 5f);
      if (v > 0) {
        emitSkid(v);
      }
    }
  }

  /* override */
  public void setHandBrake(float v) {
    if (handBrake) {
      v = Math.clamp(0, v, 1);
      activeHBrake = v;
      v = v + activeBrake;
      vw.setBrake(v * brakeForce);
      if (v > 0) {
        emitSkid(v);
      }
    }
  }

  /* override */
  public void setSteer(float v) {
    if (steer) {
      vw.setSteer(v);
    }
  }

  /* override */
  public void emitSkid(float i) {
    wantedSkid = i;
  }

  /* override */
  public void emitThrottleParticle(float i, boolean forward) {
    if (torque) {
      emitThrrotleParticleTime = 0.05f;
      if (invertSimulatedRotation) forward = !forward;
      if (forward) {
        vw.incrementRotation(Math.bySecond(burnoutAngleIntensity * i));
      } else {
        vw.incrementRotation(Math.bySecond(-burnoutAngleIntensity * i));
      }
    }
  }

  /* override */
  public boolean isTorqueWheel() {
    return torque;
  }

  public ICarMotor findMotor() {
    ICarMotor c = (ICarMotor) myObject.findComponentInParent(ICarMotor.class);
    if (c == null) {
      throw new NullPointerException("no motor found");
    }
    return c;
  }

  /* override */
  public float getHorizontalPivot() {
    return horizontalPivot;
  }
}