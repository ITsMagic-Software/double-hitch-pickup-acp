/** @Author ITsMagic */
@Order(idx = {-1})
public boolean enableController = true;

@Order(idx = {1})
public String leftKey = "left";

@Order(idx = {2})
public String rightKey = "right";

@Order(idx = {3})
public String forwardKey = "forward";

@Order(idx = {4})
public String reverseKey = "reverse";

@Order(idx = {5})
public String handBrakeKey = "handbrake";

@Order(idx = {12})
public float handBrakeWhenControllIsOff = 0.5f;

private CSKeyH lefk = new CSKeyH();
private CSKeyH rigk = new CSKeyH();
private CSKeyH fork = new CSKeyH();
private CSKeyH revk = new CSKeyH();
private CSKeyH hBrakek = new CSKeyH();

private ICarMotor motor;

/// Run only once
@Override
public void start() {
  if (motor == null) {
    motor = findMotor();
  }
}

@Override
public void stoppedRepeat() {
  fillDefaults();
}

/// Repeat every frame
@Override
public void repeat() {
  fillDefaults();
  lefk.name = leftKey;
  rigk.name = rightKey;
  fork.name = forwardKey;
  revk.name = reverseKey;
  hBrakek.name = handBrakeKey;

  if (enableController) {
    if (lefk.pressed()) {
      motor.setSteer(-1);
    } else if (rigk.pressed()) {
      motor.setSteer(1);
    } else {
      motor.setSteer(0);
    }

    if (fork.pressed()) {
      motor.setAceleration(1);
    } else if (revk.pressed()) {
      motor.setAceleration(-1);
    } else {
      motor.setAceleration(0);
    }

    motor.setHandBrake((hBrakek.pressed()) ? 1f : 0f);
  } else {
    motor.setAceleration(0);
    motor.setSteer(0);
    motor.setHandBrake(handBrakeWhenControllIsOff);
  }
}

public ICarMotor findMotor() {
  for (int x = 0; x < myObject.componentCount(); x++) {
    Component c = myObject.componentAt(x);
    if (c instanceof ICarMotor) {
      return (ICarMotor) c;
    }
  }
  return null;
}

// PUBLIC MESSAGES
public void turnOffController() {
  enableController = false;
}

public void turnOnController() {
  enableController = true;
}

void fillDefaults() {
  
}